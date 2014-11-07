package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.Mention;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

public class BAT2NIF_TranslationHelper {

    public static Document createAnnotatedDocument(String text) {
        return createAnnotatedDocument(text, null);
    }

    public static Document createAnnotatedDocument(String text,
            Set<Mention> mentions) {
        List<Marking> markings = new ArrayList<Marking>();
        if (mentions != null) {
            for (Mention mention : mentions) {
                markings.add(translateMention2Annotation(mention));
            }
        }
        return new DocumentImpl(text, markings);
    }

    public static Marking translateMention2Annotation(Mention mention) {
        return new SpanImpl(mention.getPosition(), mention.getLength());
    }
}
