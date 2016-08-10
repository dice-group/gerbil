package org.aksw.gerbil.annotator.impl.qa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJAnswers;
import org.aksw.qa.commons.load.json.EJQuestionEntry;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.aksw.qa.commons.load.json.QaldQuestionEntry;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedQALDBasedWebService extends AbstractHttpBasedAnnotator implements QASystem{

	private String name;
	private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedQALDBasedWebService.class);

	
    public static void main(String[] argc) throws GerbilException{
    	ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService("http://qanary.univ-st-etienne.fr/gerbil");
    	Document document = new DocumentImpl();
    	document.setText("Who is the wife of Barack Obama");
    	service.answerQuestion(document);
    }
    
    public ExtendedQALDBasedWebService(String url) {
        super();
        this.url = url;
    }

    public ExtendedQALDBasedWebService(String url, String name) {
        super(name);
        this.url = url;
    }
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Marking> answerQuestion(Document document)
			throws GerbilException {
		
		HttpEntity entity = new StringEntity("query="+document.getText(), "UTF-8");
        HttpPost request = null;
        try {
            request = createPostRequest(url);
        } catch (IllegalArgumentException e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        request.setEntity(entity);
        request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8");
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        entity = null;
        CloseableHttpResponse response = null;
        List<Marking> ret = null;
		try {
            response = sendRequest(request);
            // receive NIF document
            entity = response.getEntity();
            // read response and parse NIF
            try {
            	ExtendedJson exJson = (ExtendedJson) ExtendedQALDJSONLoader.readJson(entity.getContent(), ExtendedJson.class); 
 
            	List<IQuestion>  questions = EJQuestionFactory.getQuestionsFromExtendedJson(exJson);
                ret = QAUtils.translateQuestion(questions.get(0), questions.get(0).getId()+"").getMarkings();
                
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } finally {
            closeRequest(request);
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            IOUtils.closeQuietly(response);
        }
		
		return ret;
	}

}
