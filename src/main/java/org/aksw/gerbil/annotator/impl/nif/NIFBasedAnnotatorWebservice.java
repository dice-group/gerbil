/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.annotator.impl.nif;

import java.io.IOException;
import java.util.List;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.EntityTyper;
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
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFBasedAnnotatorWebservice extends AbstractHttpBasedAnnotator
        implements OKETask2Annotator, OKETask1Annotator, A2KBAnnotator, EntityTyper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFBasedAnnotatorWebservice.class);

    private static final String DOCUMENT_URI = "http://www.aksw.org/gerbil/NifWebService/request_";

    private String url;
    private int documentCount = 0;
    private NIFDocumentCreator nifCreator = new TurtleNIFDocumentCreator();
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();

    public NIFBasedAnnotatorWebservice(String url) {
        super();
        this.url = url;
    }

    public NIFBasedAnnotatorWebservice(String url, String name) {
        super(name);
        this.url = url;
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

    protected <T extends Marking> List<T> performAnnotation(Document document, Class<T> resultClass)
            throws GerbilException {
        document = request(document);
        return document.getMarkings(resultClass);
    }

    protected Document request(Document document) throws GerbilException {
        // give the document a URI
        document.setDocumentURI(DOCUMENT_URI + documentCount);
        ++documentCount;
        LOGGER.info("Started request for {}", document.getDocumentURI());
        // create NIF document
        String nifDocument = nifCreator.getDocumentAsNIFString(document);
        HttpEntity entity = new StringEntity(nifDocument, "UTF-8");
        // send NIF document
        HttpPost request = createPostRequest(url);
        request.setEntity(entity);
        request.addHeader(HttpHeaders.CONTENT_TYPE, nifCreator.getHttpContentType() + ";charset=UTF-8");
        request.addHeader(HttpHeaders.ACCEPT, nifParser.getHttpContentType());
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        entity = null;
        CloseableHttpResponse response = null;
        try {
            try {
                response = client.execute(request);
            } catch (java.net.SocketException e) {
                if (e.getMessage().contains(CONNECTION_ABORT_INDICATING_EXCPETION_MSG)) {
                    LOGGER.error("It seems like the annotator has needed too much time and has been interrupted.");
                    throw new GerbilException(
                            "It seems like the annotator has needed too much time and has been interrupted.", e,
                            ErrorTypes.ANNOTATOR_NEEDED_TOO_MUCH_TIME);
                } else {
                    LOGGER.error("Exception while sending request.", e);
                    throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
                }
            } catch (Exception e) {
                LOGGER.error("Exception while sending request.", e);
                throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.error("Response has the wrong status: " + status.toString());
                throw new GerbilException("Response has the wrong status: " + status.toString(),
                        ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            // receive NIF document
            entity = response.getEntity();
            // read response and parse NIF
            try {
                document = nifParser.getDocumentFromNIFStream(entity.getContent());
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            closeRequest(request);
        }
        LOGGER.info("Finished request for {}", document.getDocumentURI());
        return document;
    }
}
