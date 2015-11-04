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
package org.aksw.gerbil.utils.bat;

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.ScoredAnnotation;
import it.unipi.di.acube.batframework.data.ScoredTag;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.systemPlugins.DBPediaApi;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;

import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.ScoredMeaning;
import org.aksw.gerbil.transfer.nif.Span;

@SuppressWarnings("deprecation")
public class NIF2BAT_TranslationHelper {

    public static HashSet<Annotation> createAnnotations(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi,
            Document document) {
        return translateMarking(Annotation.class, wikiApi, dbpediaApi, document);
    }

    public static HashSet<ScoredAnnotation> createScoredAnnotations(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, Document document) {
        return translateMarking(ScoredAnnotation.class, wikiApi, dbpediaApi, document);
    }

    public static HashSet<Tag> createTags(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, Document document) {
        return translateMarking(Tag.class, wikiApi, dbpediaApi, document);
    }

    public static HashSet<ScoredTag> createScoredTags(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi,
            Document document) {
        return translateMarking(ScoredTag.class, wikiApi, dbpediaApi, document);
    }

    public static HashSet<Mention> createMentions(Document document) {
        return translateMarking(Mention.class, null, null, document);
    }

    @SuppressWarnings("unchecked")
    protected static <T> HashSet<T> translateMarking(Class<T> clazz, WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, Document document) {
        List<Marking> markings = document.getMarkings();
        HashSet<T> tags = new HashSet<T>();
        Object o;
        for (Marking marking : markings) {
            o = translateMarking(wikiApi, dbpediaApi, marking);
            if ((o != null) && (clazz.isInstance(o))) {
                tags.add((T) o);
            }
        }
        return tags;
    }

    protected static Object translateMarking(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, Marking marking) {
        if (marking instanceof Span) {
            if ((marking instanceof Meaning) && (wikiApi != null)) {
                int wikiId = DBpediaToWikiId.getId(wikiApi, ((Meaning) marking).getUri());
                if (marking instanceof ScoredMeaning) {
                    return new ScoredAnnotation(((Span) marking).getStartPosition(), ((Span) marking).getLength(),
                            wikiId, (float) ((ScoredMeaning) marking).getConfidence());
                } else {
                    return new Annotation(((Span) marking).getStartPosition(), ((Span) marking).getLength(), wikiId);
                }
            } else {
                return new Mention(((Span) marking).getStartPosition(), ((Span) marking).getLength());
            }
        } else if ((marking instanceof Meaning) && (wikiApi != null)) {
            int wikiId = DBpediaToWikiId.getId(wikiApi, ((Meaning) marking).getUri());
            if (marking instanceof ScoredMeaning) {
                return new ScoredTag(wikiId, (float) ((ScoredMeaning) marking).getConfidence());
            } else {
                return new Tag(wikiId);
            }
        }
        return null;
    }
}
