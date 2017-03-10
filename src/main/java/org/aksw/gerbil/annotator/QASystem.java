package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

public interface QASystem extends Annotator {

    public List<Marking> answerQuestion(Document document, String questionLang) throws GerbilException;

}
