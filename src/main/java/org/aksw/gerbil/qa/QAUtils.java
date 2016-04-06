package org.aksw.gerbil.qa;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;

public class QAUtils {

    public static final String QUESTION_LANGUAGE = "en";

    public static Document translateQuestion(IQuestion question, String questionUri) {
        Document document = new DocumentImpl(question.getLanguageToQuestion().get(QUESTION_LANGUAGE), questionUri);
        // FIXME Ricardo, add the needed markings to the document
        return document;
    }
}
