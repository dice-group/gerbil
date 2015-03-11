package org.aksw.gerbil.utils.bat;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.ScoredMeaning;
import org.aksw.gerbil.transfer.nif.Span;

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