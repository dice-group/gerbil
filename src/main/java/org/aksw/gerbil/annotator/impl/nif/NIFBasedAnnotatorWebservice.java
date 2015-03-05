/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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

import it.acubelab.batframework.utils.AnnotationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.NIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFBasedAnnotatorWebservice implements EntityExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFBasedAnnotatorWebservice.class);

    private static final String DOCUMENT_URI = "http://www.aksw.org/gerbil/NifWebService/request_";

    private String url;
    private String name;
    private CloseableHttpClient client;
    private int documentCount = 0;
    private NIFDocumentCreator nifCreator = new TurtleNIFDocumentCreator();
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();

    public NIFBasedAnnotatorWebservice(String url, String name) {
        this.url = url;
        this.name = name;
        client = HttpClients.createDefault();
    }

    @Override
    public String getName() {
        return name;
    }

    // @Override
    // public HashSet<Annotation> solveD2W(String text, HashSet<Mention>
    // mentions)
    // throws AnnotationException {
    // // translate the mentions into an AnnotatedDocument object
    // Document document = BAT2NIF_TranslationHelper
    // .createAnnotatedDocument(text, mentions);
    // document = request(document);
    // // translate the annotated document into a HashSet of BAT Annotations
    // return NIF2BAT_TranslationHelper.createAnnotations(wikiApi, dbpediaApi,
    // document);
    // }
    //
    // @Override
    // public HashSet<Annotation> solveA2W(String text) throws
    // AnnotationException {
    // // translate the mentions into an AnnotatedDocument object
    // Document document = BAT2NIF_TranslationHelper
    // .createAnnotatedDocument(text);
    // document = request(document);
    // // translate the annotated document into a HashSet of BAT Annotations
    // return NIF2BAT_TranslationHelper.createAnnotations(wikiApi, dbpediaApi,
    // document);
    // }
    //
    // @Override
    // public HashSet<Tag> solveC2W(String text) throws AnnotationException {
    // // translate the mentions into an AnnotatedDocument object
    // Document document = BAT2NIF_TranslationHelper
    // .createAnnotatedDocument(text);
    // document = request(document);
    // // translate the annotated document into a HashSet of BAT Annotations
    // return NIF2BAT_TranslationHelper.createTags(wikiApi, dbpediaApi,
    // document);
    // }
    //
    // @Override
    // public HashSet<ScoredTag> solveSc2W(String text) throws
    // AnnotationException {
    // // translate the mentions into an AnnotatedDocument object
    // Document document = BAT2NIF_TranslationHelper
    // .createAnnotatedDocument(text);
    // document = request(document);
    // // translate the annotated document into a HashSet of BAT Annotations
    // return NIF2BAT_TranslationHelper.createScoredTags(wikiApi, dbpediaApi,
    // document);
    // }

    @Override
    public List<NamedEntity> performLinking(Document document) throws GerbilException {
        document = request(document);
        // transform all Spans into NamedEntity instances with uri=null
        return document.getMarkings(NamedEntity.class);
    }

    @Override
    public List<Span> performRecognition(Document document) {
        document = request(document);
        return document.getMarkings(Span.class);
    }

    @Override
    public List<NamedEntity> performExtraction(Document document) {
        document = request(document);
        // transform all Spans into NamedEntity instances with uri=null
        return document.getMarkings(NamedEntity.class);
    }

    // @Override
    // public HashSet<ScoredAnnotation> solveSa2W(String text) throws
    // AnnotationException {
    // // translate the mentions into an AnnotatedDocument object
    // Document document =
    // BAT2NIF_TranslationHelper.createAnnotatedDocument(text);
    // document = request(document);
    // // translate the annotated document into a HashSet of BAT Annotations
    // return NIF2BAT_TranslationHelper.createScoredAnnotations(wikiApi,
    // dbpediaApi, document);
    // }

    protected Document request(Document document) {
        // give the document a URI
        document.setDocumentURI(DOCUMENT_URI + documentCount);
        ++documentCount;
        LOGGER.info("Started request for {}", document.getDocumentURI());
        // create NIF document
        String nifDocument = nifCreator.getDocumentAsNIFString(document);
        HttpEntity entity = null;
        try {
            entity = new StringEntity(nifDocument, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception while creating POST request.", e);
            throw new AnnotationException("Exception while creating POST request. " + e.getLocalizedMessage());
        }
        // send NIF document (start time measure)
        // lastRequestSend = System.currentTimeMillis();
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);
        request.addHeader("Content-Type", nifCreator.getHttpContentType());
        request.addHeader("Accept", nifParser.getHttpContentType());

        entity = null;
        CloseableHttpResponse response = null;
        InputStreamReader reader = null;
        try {
            try {
                response = client.execute(request);
            } catch (Exception e) {
                LOGGER.error("Exception while sending request.", e);
                throw new AnnotationException("Exception while sending request. " + e.getLocalizedMessage());
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.error("Response has the wrong status: " + status.toString());
                throw new AnnotationException("Response has the wrong status: " + status.toString());
            }
            // receive NIF document (end time measure and set time)
            entity = response.getEntity();
            // lastResponseReceived = System.currentTimeMillis();
            // read response and parse NIF
            try {
                reader = new InputStreamReader(entity.getContent());
                document = nifParser.getDocumentFromNIFReader(reader);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new AnnotationException("Couldn't parse the response. " + e.getLocalizedMessage());
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
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
        }
        LOGGER.info("Finished request for {}", document.getDocumentURI());
        return document;
    }
}
