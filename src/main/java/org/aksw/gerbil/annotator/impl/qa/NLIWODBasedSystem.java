package org.aksw.gerbil.annotator.impl.qa;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.systems.ASystem;
import org.aksw.qa.systems.HAWK;
import org.aksw.qa.systems.QAKIS;
import org.aksw.qa.systems.SINA;
import org.aksw.qa.systems.START;
import org.aksw.qa.systems.YODA;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Prologue;

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
        IQuestion question = qaSystem.search(document.getText());
        if(question.getSparqlQuery()!=null)
        	question.setSparqlQuery(replacePrefixes(question.getSparqlQuery(), PrefixMapping.Extended));
        Document resultDoc = QAUtils.translateQuestion(question, document.getDocumentURI());
        if(resultDoc != null) {
            return resultDoc.getMarkings();
        } else {
            return new ArrayList<Marking>(0);
        }
    }

    //TODO find a better class for this method
    public static String replacePrefixes(String query, PrefixMapping pmap){
        /* With Prologue and the parse method, 
         * the queryString gets parsed without an error
         */
        Prologue prog = new Prologue();
        prog.setPrefixMapping(pmap);
        Query q = QueryFactory.parse(new Query(prog), query, null, null);
        //Set Prefix Mapping
        q.setPrefixMapping(pmap);
        //remove PrefixMapping so the prefixes will get replaced by the full uris
        q.setPrefixMapping(null);       
        return q.serialize();
    }
    
}
