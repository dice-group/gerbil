package org.aksw.gerbil.annotator.impl.qa;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.qa.systems.ASystem;
import org.aksw.qa.systems.HAWK;
import org.aksw.qa.systems.QAKIS;
import org.aksw.qa.systems.SINA;
import org.aksw.qa.systems.START;
import org.aksw.qa.systems.YODA;

public class NLIWODBasedSystem extends AbstractAnnotator implements QASystem {

    public static final String HAWK_SYSTEM_NAME = "HAWK";
    public static final String QAKIS_SYSTEM_NAME = "QAKIS";
    public static final String SINA_SYSTEM_NAME = "SINA";
    public static final String START_SYSTEM_NAME = "START";
    public static final String YODA_SYSTEM_NAME = "YODA";

    protected ASystem qaSystem;

    public NLIWODBasedSystem(String systemName) throws GerbilException {
        switch (systemName) {
        case HAWK_SYSTEM_NAME: {
            qaSystem = new HAWK();
            break;
        }
        case QAKIS_SYSTEM_NAME: {
            qaSystem = new QAKIS();
            break;
        }
        case SINA_SYSTEM_NAME: {
            qaSystem = new SINA();
            break;
        }
        case START_SYSTEM_NAME: {
            qaSystem = new START();
            break;
        }
        case YODA_SYSTEM_NAME: {
            qaSystem = new YODA();
            break;
        }
        default:
            throw new GerbilException("Got an unknown system name \"" + systemName + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
    }

    @Override
    public List<Marking> answerQuestion(Document document) throws GerbilException {
        return Arrays.asList((Marking) new AnswerSet(qaSystem.search(document.getText())));
    }

}
