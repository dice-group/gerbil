/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.annotator.impl.nif;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKE2018Task4Annotator;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.NIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFBasedAnnotatorWebservice extends AbstractHttpBasedAnnotator implements OKETask2Annotator,
        OKETask1Annotator, A2KBAnnotator, EntityTyper, OKE2018Task4Annotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFBasedAnnotatorWebservice.class);

    private static final String DOCUMENT_URI = "http://www.aksw.org/gerbil/NifWebService/request_";

    private String url;
    private int documentCount = 0;
    private NIFDocumentCreator nifCreator = new TurtleNIFDocumentCreator();
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();

    private List<Header> additionalHeader = new LinkedList<Header>();
    
    public NIFBasedAnnotatorWebservice(String url) {
        super();
        this.url = url;
    }

    public NIFBasedAnnotatorWebservice(String url, String name) {
        super(name);
        this.url = url;
    }
    
    public NIFBasedAnnotatorWebservice(String url, String name, String additionalHeader) {
    	this(url, name);
    	createHeader(additionalHeader);
    }
    
    private void createHeader(String additionalHeader) {
    	if(additionalHeader.isEmpty()) {
    		return;
    	}
    	//format is "-H name value -H name value ..."
    	String[] nameValuePairs = additionalHeader.trim().split("-H\\s+");
    	for(String nameValuePair : nameValuePairs) {
    		if(nameValuePair.isEmpty()) {
    			continue;
    		}
    		String[] pair = nameValuePair.trim().split("\\s+"); 
    		if(pair.length!=2) {
    			//wrong format
    			LOGGER.warn("Additional Header has wrong format {}", nameValuePair);
    			continue;
    		}
    		this.getAdditionalHeader().add(new BasicHeader(pair[0], pair[1]));
    		LOGGER.info("Set additional header: {}", nameValuePair);
    	}
    }
    

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return performAnnotation(document, Meaning.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return performAnnotation(document, Span.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return performAnnotation(document, TypedSpan.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return performAnnotation(document, TypedNamedEntity.class);
    }

    @Override
    public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
        return performAnnotation(document, TypedNamedEntity.class);
    }

    @Override
    public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
        return performAnnotation(document, TypedSpan.class);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Marking> List<T> performAnnotation(Document document, Class<T> resultClass)
            throws GerbilException {
        document = request(document);
        if(document != null) {
            return document.getMarkings(resultClass);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public Document request(Document document) throws GerbilException {
        // give the document a URI
        document.setDocumentURI(DOCUMENT_URI + documentCount);
        ++documentCount;
        LOGGER.info("Started request for {} (\"{}\")", document.getDocumentURI(),
                document.getText().length() > 20 ? (document.getText().substring(0, 20) + "...") : document.getText());
        // create NIF document
        String nifDocument = nifCreator.getDocumentAsNIFString(document);
        System.out.println(nifDocument);
        HttpEntity entity = new StringEntity(nifDocument, "UTF-8");
        // send NIF document
        HttpPost request = null;
        try {
            request = createPostRequest(url);
        } catch (IllegalArgumentException e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        request.setEntity(entity);
        request.addHeader(HttpHeaders.CONTENT_TYPE, nifCreator.getHttpContentType() + ";charset=UTF-8");
        request.addHeader(HttpHeaders.ACCEPT, nifParser.getHttpContentType());
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        for(Header header : getAdditionalHeader()) {
        	request.addHeader(header);
        }
        
        entity = null;
        CloseableHttpResponse response = null;
        Document responseDoc = null;
        try {
            response = sendRequest(request, true);
            // receive NIF document
            entity = response.getEntity();
            // read response and parse NIF
            try {
                responseDoc = nifParser.getDocumentFromNIFStream(entity.getContent());
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
        LOGGER.info("Finished request for {}", document.getDocumentURI());
        return responseDoc;
    }

    public String getUrl() {
        return url;
    }

	@Override
	public List<Relation> performRETask(Document document) throws GerbilException {
        return performAnnotation(document, Relation.class);

	}

	@Override
	public List<Marking> performOKE2018Task4(Document document) throws GerbilException {
        return performAnnotation(document, Marking.class);

	}

	/**
	 * @return the additionalHeader
	 */
	public List<Header> getAdditionalHeader() {
		return additionalHeader;
	}

	/**
	 * @param additionalHeader the additionalHeader to set
	 */
	public void setAdditionalHeader(List<Header> additionalHeader) {
		this.additionalHeader = additionalHeader;
	}

}
