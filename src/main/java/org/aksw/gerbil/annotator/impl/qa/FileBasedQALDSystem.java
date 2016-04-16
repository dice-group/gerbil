package org.aksw.gerbil.annotator.impl.qa;

import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

public class FileBasedQALDSystem implements QASystem {
    
    public FileBasedQALDSystem() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<Marking> answerQuestion(Document document) throws GerbilException {
        return null;
    }

}
