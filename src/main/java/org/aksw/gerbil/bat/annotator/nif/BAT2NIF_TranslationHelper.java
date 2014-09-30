package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.Mention;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.Annotation;
import org.aksw.gerbil.transfer.nif.data.AnnotatedDocumentImpl;
import org.aksw.gerbil.transfer.nif.data.AnnotationImpl;

public class BAT2NIF_TranslationHelper {

    public static AnnotatedDocument createAnnotatedDocument(String text) {
        return createAnnotatedDocument(text, null);
    }

    public static AnnotatedDocument createAnnotatedDocument(String text,
            HashSet<Mention> mentions) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        if (mentions != null) {
            for (Mention mention : mentions) {
                annotations.add(translateMention2Annotation(mention));
            }
        }
        return new AnnotatedDocumentImpl(text, annotations);
    }

    public static Annotation translateMention2Annotation(Mention mention) {
        return new AnnotationImpl(mention.getPosition(), mention.getLength());
    }
}
