package org.aksw.gerbil.bat.annotator.nif;

import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.bat.converter.DBpediaToWikiId;
import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;

public class NIF2BAT_TranslationHelper {

    public static HashSet<it.acubelab.batframework.data.Annotation> createAnnotations(
            AnnotatedDocument document) {
        List<Annotation> annotations = document.getAnnotations();
        HashSet<it.acubelab.batframework.data.Annotation> batAnnotations = new HashSet<it.acubelab.batframework.data.Annotation>();
        for (Annotation annotation : annotations) {
            batAnnotations.add(translateAnnotation2BatAnnotation(annotation));
        }
        return null;
    }

    public static it.acubelab.batframework.data.Annotation translateAnnotation2BatAnnotation(
            Annotation annotation) {
        int wikiId = -1;
        // if this is a disambiguated annotation
        if (annotation instanceof DisambiguatedAnnotation) {
            DBpediaToWikiId.getId(((DisambiguatedAnnotation) annotation).getUri());
        }
        return new it.acubelab.batframework.data.Annotation(annotation.getStartPosition(), annotation.getLength(),
                wikiId);
    }
}
