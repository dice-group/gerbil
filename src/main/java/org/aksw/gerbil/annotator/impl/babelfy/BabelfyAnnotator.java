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
package org.aksw.gerbil.annotator.impl.babelfy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;

import it.uniroma1.lcl.babelfy.commons.BabelfyConfiguration;
import it.uniroma1.lcl.babelfy.commons.BabelfyConstraints;
import it.uniroma1.lcl.babelfy.commons.BabelfyParameters;
import it.uniroma1.lcl.babelfy.commons.IBabelfy;
import it.uniroma1.lcl.babelfy.commons.annotation.CharOffsetFragment;
import it.uniroma1.lcl.babelfy.commons.annotation.DisambiguationConstraint;
import it.uniroma1.lcl.babelfy.commons.annotation.MCS;
import it.uniroma1.lcl.babelfy.commons.annotation.PoStaggingOptions;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotationResource;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotationType;
import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.jlt.util.Language;

/**
 * The BabelFy Annotator.
 * 
 * <p>
 * <i>Andrea Moro: "I recommend to use the maximum amount of available
 * characters (3500) at each request (i.e., try to put a document all together
 * or split it in chunks of 3500 characters) both for scalability and
 * performance reasons."</i><br>
 * This means, that we have to split up documents longer than 3500 characters.
 * Unfortunately, BabelFy seems to measure the length on the escaped text which
 * means that every text could be three times longer than the unescaped text.
 * Thus, we have to set {@link #BABELFY_MAX_TEXT_LENGTH}=
 * {@value #BABELFY_MAX_TEXT_LENGTH}.
 * </p>
 */
public class BabelfyAnnotator extends AbstractAnnotator implements A2KBAnnotator {

    private static final String BABELNET_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.Babelfy.configFile";
    private static final String BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME = "org.aksw.gerbil.annotators.Babelfy.key";
    private static final int BABELFY_MAX_TEXT_LENGTH = 1100;

    private String key;

    public BabelfyAnnotator() throws GerbilException {
        String configFile = GerbilConfiguration.getInstance().getString(BABELNET_CONFIG_FILE_PROPERTY_NAME);
        if (configFile == null) {
            throw new GerbilException("Couldn't load needed Property \"" + BABELNET_CONFIG_FILE_PROPERTY_NAME + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        // Load the configuration
        BabelfyConfiguration.getInstance().setConfigurationFile(new File(configFile));
        // Load and use the key if there is one
        key = GerbilConfiguration.getInstance().getString(BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME);
        if (key == null) {
            key = "";
        }
        BabelfyConfiguration.getInstance().setRFkey(key);
    }

    public BabelfyAnnotator(String configFile) throws GerbilException {
        // Load the configuration
        BabelfyConfiguration.getInstance().setConfigurationFile(new File(configFile));
        // Load and use the key if there is one
        key = GerbilConfiguration.getInstance().getString(BABELFY_WEB_SERVICE_KEY_PROPERTY_NAME);
        if (key == null) {
            key = "";
        }
        BabelfyConfiguration.getInstance().setRFkey(key);
    }

    public BabelfyAnnotator(String key, String configFile) {
        this.key = key;
        BabelfyConfiguration.getInstance().setConfigurationFile(new File(configFile));
        BabelfyConfiguration.getInstance().setRFkey(key);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return sendRequest(document, false).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return sendRequest(document, false).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return sendRequest(document, true).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return sendRequest(document, false).getMarkings(Span.class);
    }

    protected Document sendRequest(Document document, boolean useSpans) throws GerbilException {
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());
        List<Span> mentions = null;
        List<String> chunks = splitText(document.getText());
        BabelfyParameters bfyParameters = new BabelfyParameters();
        bfyParameters.setAnnotationResource(SemanticAnnotationResource.WIKI);
        bfyParameters.setMCS(MCS.OFF);
        bfyParameters.setExtendCandidatesWithAIDAmeans(true);
        if (useSpans) {
            bfyParameters.setThreshold(0.0);
            bfyParameters.setPoStaggingOptions(PoStaggingOptions.INPUT_FRAGMENTS_AS_NOUNS);
            bfyParameters.setDisambiguationConstraint(DisambiguationConstraint.DISAMBIGUATE_ALL_RETURN_INPUT_FRAGMENTS);
            mentions = document.getMarkings(Span.class);
        } else {
            bfyParameters.setThreshold(0.3);
            bfyParameters.setAnnotationType(SemanticAnnotationType.NAMED_ENTITIES);
        }

        IBabelfy bfy = new Babelfy(bfyParameters);
        int prevChars = 0;
        for (String chunk : chunks) {
            BabelfyConstraints constraints = new BabelfyConstraints();
            if (useSpans) {
                for (Span span : mentions) {
                    if (span.getStartPosition() >= prevChars
                            && (span.getStartPosition() + span.getLength()) < prevChars + chunk.length()) {
                        constraints
                                .addFragmentToDisambiguate(new CharOffsetFragment(span.getStartPosition() - prevChars,
                                        (span.getStartPosition() + span.getLength()) - 1 - prevChars));
                    }
                }
            }
            List<SemanticAnnotation> bfyAnnotations = sendRequest(bfy, chunk, constraints);

            for (SemanticAnnotation bfyAnn : bfyAnnotations) {
                resultDoc.addMarking(new ScoredNamedEntity(prevChars + bfyAnn.getCharOffsetFragment().getStart(),
                        bfyAnn.getCharOffsetFragment().getEnd() - bfyAnn.getCharOffsetFragment().getStart() + 1,
                        bfyAnn.getDBpediaURL(), bfyAnn.getScore()));
            }
            prevChars += chunk.length();
        }
        return resultDoc;
    }

    protected synchronized List<SemanticAnnotation> sendRequest(IBabelfy bfy, String chunk,
            BabelfyConstraints constraints) {
        return bfy.babelfy(chunk, Language.EN, constraints);
    }

    protected List<String> splitText(String text) {
        List<String> chunks = new ArrayList<String>();
        int start = 0, end = 0, nextEnd = 0;
        // As long as we have to create chunks
        while ((nextEnd >= 0) && ((text.length() - nextEnd) > BABELFY_MAX_TEXT_LENGTH)) {
            // We have to use the next space, even it would be too far away
            end = nextEnd = text.indexOf(' ', start + 1);
            // Search for the next possible end this chunk
            while ((nextEnd >= 0) && ((nextEnd - start) < BABELFY_MAX_TEXT_LENGTH)) {
                end = nextEnd;
                nextEnd = text.indexOf(' ', end + 1);
            }
            // Add the chunk
            chunks.add(text.substring(start, end));
            start = end;
        }
        // Add the last chunk
        chunks.add(text.substring(start));
        return chunks;
    }

}
