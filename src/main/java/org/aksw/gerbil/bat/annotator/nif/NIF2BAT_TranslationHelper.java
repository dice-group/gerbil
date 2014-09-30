package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredDisambigAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIF2BAT_TranslationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIF2BAT_TranslationHelper.class);

    public static HashSet<it.acubelab.batframework.data.Annotation> createAnnotations(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, AnnotatedDocument document) {
        List<Annotation> annotations = document.getAnnotations();
        HashSet<it.acubelab.batframework.data.Annotation> batAnnotations = new HashSet<it.acubelab.batframework.data.Annotation>();
        for (Annotation annotation : annotations) {
            batAnnotations
                    .add((it.acubelab.batframework.data.Annotation) translateAnnotation2BatAnnotation(wikiApi,
                            dbpediaApi, annotation));
        }
        return batAnnotations;
    }

    // public static it.acubelab.batframework.data.Annotation translateAnnotation2BatAnnotation(
    // Annotation annotation) {
    // int wikiId = -1;
    // // if this is a disambiguated annotation
    // if (annotation instanceof DisambiguatedAnnotation) {
    // wikiId = DBpediaToWikiId.getId(((DisambiguatedAnnotation) annotation).getUri());
    // }
    // return new it.acubelab.batframework.data.Annotation(annotation.getStartPosition(), annotation.getLength(),
    // wikiId);
    // }

    public static HashSet<ScoredAnnotation> createScoredAnnotations(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, AnnotatedDocument document) {
        List<Annotation> annotations = document.getAnnotations();
        HashSet<ScoredAnnotation> batAnnotations = new HashSet<ScoredAnnotation>();
        for (Annotation annotation : annotations) {
            batAnnotations.add((ScoredAnnotation) translateAnnotation2BatAnnotation(wikiApi, dbpediaApi, annotation));
        }
        return batAnnotations;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Tag> T translateAnnotation2BatAnnotation(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, Annotation annotation) {
        String wikipediaTitle = null;
        if (annotation instanceof DisambiguatedAnnotation) {
            wikipediaTitle = dbpediaApi.dbpediaToWikipedia(((DisambiguatedAnnotation) annotation).getUri().replace(
                    "http://dbpedia.org/resource/", ""));
            int wikiId = -1;
            if (wikipediaTitle != null) {
                try {
                    wikiId = wikiApi.getIdByTitle(wikipediaTitle);
                } catch (IOException e) {
                    LOGGER.error("Error while requesting wikipedia Id from the API. Setting it to -1.");
                }
            }
            // int wikiId = DBpediaToWikiId.getId(((DisambiguatedAnnotation) annotation).getUri());
            System.out.println(((DisambiguatedAnnotation) annotation).getUri() + " --> " + wikiId);
            if (annotation instanceof ScoredDisambigAnnotation) {
                return (T) new ScoredAnnotation(annotation.getStartPosition(), annotation.getLength(), wikiId,
                        (float) ((ScoredDisambigAnnotation) annotation).getConfidence());
            } else {
                return (T) new it.acubelab.batframework.data.Annotation(annotation.getStartPosition(),
                        annotation.getLength(), wikiId);
            }
        } else {
            return null;
        }
    }

    public static HashSet<Tag> createTags(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, AnnotatedDocument document) {
        List<Annotation> annotations = document.getAnnotations();
        HashSet<Tag> batAnnotations = new HashSet<Tag>();
        for (Annotation annotation : annotations) {
            batAnnotations.add(translateAnnotation2BatAnnotation(wikiApi, dbpediaApi, annotation));
        }
        return batAnnotations;
    }

    public static HashSet<ScoredTag> createScoredTags(WikipediaApiInterface wikiApi,
            DBPediaApi dbpediaApi, AnnotatedDocument document) {
        List<Annotation> annotations = document.getAnnotations();
        HashSet<ScoredTag> batAnnotations = new HashSet<ScoredTag>();
        for (Annotation annotation : annotations) {
            batAnnotations.add((ScoredTag) translateAnnotation2BatAnnotation(wikiApi, dbpediaApi, annotation));
        }
        return batAnnotations;
    }
}
