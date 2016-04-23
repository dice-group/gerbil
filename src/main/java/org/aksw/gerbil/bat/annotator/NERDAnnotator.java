/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
//   NERD Annotator - It triggers queries to the NERD framework 
// 	 http://nerd.eurecom.fr and it parses the results
//
//   Copyright 2014 EURECOM
//
//   Authors:
//      Giuseppe Rizzo <giuse.rizzo@gmail.com>
//
//   Licensed under ...
package org.aksw.gerbil.bat.annotator;

import fr.eurecom.nerd.client.NERD;
import fr.eurecom.nerd.client.schema.Entity;
import fr.eurecom.nerd.client.type.DocumentType;
import fr.eurecom.nerd.client.type.ExtractorType;
import fr.eurecom.nerd.client.type.GranularityType;
import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.ScoredAnnotation;
import it.unipi.di.acube.batframework.data.ScoredTag;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.Sa2WSystem;
import it.unipi.di.acube.batframework.utils.AnnotationException;
import it.unipi.di.acube.batframework.utils.ProblemReduction;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * NERD Annotator - It triggers queries to the NERD framework http://nerd.eurecom.fr and it parses the results
 * 
 * @author Giuseppe Rizzo (giuse.rizzo@gmail.com)
 * 
 */
@Deprecated
public class NERDAnnotator implements Sa2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NERDAnnotator.class);
    public static final String NAME = "NERD-ML";
    private static String NERD_API_PROPERTY_NAME = "org.aksw.gerbil.annotators.nerd.api";
    private final String NERD_API = GerbilConfiguration.getInstance().getString(NERD_API_PROPERTY_NAME);

    private String key;

    @Autowired
    private WikipediaApiInterface wikiApi;

    /**
     * Shouldn't be used until we have finished porting the project to Spring.
     */
    @Deprecated
    public NERDAnnotator(String key) {
        this.key = key;
    }

    public NERDAnnotator(WikipediaApiInterface wikiApi, String key) {
        this.key = key;
        this.wikiApi = wikiApi;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException
    {
        return ProblemReduction.Sa2WToA2W(solveSa2W(text), Float.MIN_VALUE);
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException
    {
        return ProblemReduction.A2WToC2W(solveA2W(text));
    }

    @Override
    public long getLastAnnotationTime()
    {
         return -1;
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException
    {
        return ProblemReduction.Sa2WToSc2W(this.solveSa2W(text));
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException
    {
        return getNERDAnnotations(text);
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
            throws AnnotationException
    {
        return ProblemReduction.Sa2WToD2W(getNERDAnnotations(text), mentions, 0.1f);

        // HashSet<ScoredAnnotation> anns = getNERDAnnotations(text);
        // HashSet<Annotation> result = new HashSet<Annotation>();
        //
        // //FIXME
        // //naive implementation that iterates through the list of mentions and gets,
        // //if available, the wiki link for that mention
        // for (Mention m : mentions) {
        // for (ScoredAnnotation a : anns)
        // {
        // if( m.getPosition() == a.getPosition() )
        // result.add(new Annotation(a.getPosition(), a.getLength(), a.getConcept()));
        // }
        // }
        //
        // return result;
    }

    /**
     * Send request to NERD and parse the response as a set of scored annotations.
     * 
     * @param text
     *            the text to send
     */
    public HashSet<ScoredAnnotation> getNERDAnnotations(String text)
    {
        HashSet<ScoredAnnotation> annotations = Sets.newHashSet();
        try {
            // lastTime = Calendar.getInstance().getTimeInMillis();

            LOGGER.debug("shipping to NERD the text to annotate");

            NERD nerd = new NERD(NERD_API, key);
            List<Entity> entities = nerd.annotate(ExtractorType.NERDML,
                    DocumentType.PLAINTEXT,
                    text,
                    GranularityType.OEN,
                    60L,
                    true,
                    true);

            LOGGER.debug("NERD has found {} entities", entities.size());

            for (Entity e : entities) {
                int id = DBpediaToWikiId.getId(wikiApi, e.getUri());

                annotations.add(new ScoredAnnotation(
                        e.getStartChar(),
                        e.getEndChar() - e.getStartChar(),
                        id,
                        new Float(e.getConfidence()))
                        );
            }
        } catch (Exception e) {
            e.printStackTrace();

            // TODO
            // fix the error handling in order to closely check what is the source of the error
            throw new AnnotationException("An error occurred while querying " +
                    this.getName() +
                    " API. Message: " +
                    e.getMessage());
        }

        return annotations;

    }
}
