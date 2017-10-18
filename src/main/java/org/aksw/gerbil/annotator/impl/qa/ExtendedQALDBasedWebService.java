package org.aksw.gerbil.annotator.impl.qa;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class ExtendedQALDBasedWebService extends AbstractHttpBasedAnnotator implements QASystem{

	private String name;
	private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedQALDBasedWebService.class);

	
    public static void main(String[] argc) throws GerbilException, IOException{
    	ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService("http://wdaqua-qanary.univ-st-etienne.fr/gerbil");
    	Document document = new DocumentImpl();
    	document.setText("When was Barack Obama born?");
    	service.answerQuestion(document, "en");
    	service.close();
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
	public List<Marking> answerQuestion(Document document, String questionLang)
			throws GerbilException {
		
		HttpEntity entity = new StringEntity("query="+document.getText()+"&lang="+questionLang, "UTF-8");
		
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
        	String content = EntityUtils.toString(entity);
        	InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        	Object json = null;
        	try {
        		json = ExtendedQALDJSONLoader.readJson(stream, ExtendedJson.class); 
        	}catch(UnrecognizedPropertyException e) {
        		//can be ignored, it is just not an extended json obnject
        	}
        	List<IQuestion>  questions;
        	if(json==null){
        	    stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        	    QaldJson qaldJson = (QaldJson) ExtendedQALDJSONLoader.readJson(stream, QaldJson.class);
        	    questions = EJQuestionFactory.getQuestionsFromQaldJson(qaldJson);
        	}
        	else{
        	    ExtendedJson exJson = (ExtendedJson) json;
        	    questions = EJQuestionFactory.getQuestionsFromExtendedJson(exJson);

        	}
        	
            	Document resultDoc = QAUtils.translateQuestion(questions.get(0), null, questionLang);
                ret = resultDoc.getMarkings();
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
