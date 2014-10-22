package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.AnnotationException;
import it.acubelab.batframework.utils.ProblemReduction;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.NIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
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

public class NIFBasedAnnotatorWebservice implements Sa2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFBasedAnnotatorWebservice.class);

    private static final String DOCUMENT_URI = "http://www.aksw.org/gerbil/NifWebService/request_";

    private String url;
    private String name;
    private CloseableHttpClient client;
    private long lastRequestSend = 0;
    private long lastResponseReceived = 0;
    private int documentCount = 0;
    private NIFDocumentCreator nifCreator = new TurtleNIFDocumentCreator();
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();
    private WikipediaApiInterface wikiApi;
    private DBPediaApi dbpediaApi;

    public NIFBasedAnnotatorWebservice(String url, String name, WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi) {
        this.url = url;
        this.name = name;
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
        client = HttpClients.createDefault();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns -1 if an error occurred while requesting the annotations.
     */
    @Override
    public long getLastAnnotationTime() {
        if (lastRequestSend < lastResponseReceived) {
            return lastResponseReceived - lastRequestSend;
        } else {
            return -1L;
        }
    }

    @Override
    public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions)
            throws AnnotationException {
        // translate the mentions into an AnnotatedDocument object
        AnnotatedDocument document = BAT2NIF_TranslationHelper
                .createAnnotatedDocument(text, mentions);
        document = request(document);
        // translate the annotated document into a HashSet of BAT Annotations
        return NIF2BAT_TranslationHelper.createAnnotations(wikiApi, dbpediaApi, document);
    }

    @Override
    public HashSet<Annotation> solveA2W(String text) throws AnnotationException {
        // translate the mentions into an AnnotatedDocument object
        AnnotatedDocument document = BAT2NIF_TranslationHelper
                .createAnnotatedDocument(text);
        document = request(document);
        // translate the annotated document into a HashSet of BAT Annotations
        return NIF2BAT_TranslationHelper.createAnnotations(wikiApi, dbpediaApi, document);
    }

    @Override
    public HashSet<Tag> solveC2W(String text) throws AnnotationException {
        // translate the mentions into an AnnotatedDocument object
        AnnotatedDocument document = BAT2NIF_TranslationHelper
                .createAnnotatedDocument(text);
        document = request(document);
        // translate the annotated document into a HashSet of BAT Annotations
        return NIF2BAT_TranslationHelper.createTags(wikiApi, dbpediaApi, document);
    }

    @Override
    public HashSet<ScoredTag> solveSc2W(String text) throws AnnotationException {
        // translate the mentions into an AnnotatedDocument object
        AnnotatedDocument document = BAT2NIF_TranslationHelper
                .createAnnotatedDocument(text);
        document = request(document);
        // translate the annotated document into a HashSet of BAT Annotations
        return NIF2BAT_TranslationHelper.createScoredTags(wikiApi, dbpediaApi, document);
    }

    @Override
    public HashSet<ScoredAnnotation> solveSa2W(String text) throws AnnotationException {
        // translate the mentions into an AnnotatedDocument object
        AnnotatedDocument document = BAT2NIF_TranslationHelper
                .createAnnotatedDocument(text);
        document = request(document);
        // translate the annotated document into a HashSet of BAT Annotations
        return NIF2BAT_TranslationHelper.createScoredAnnotations(wikiApi, dbpediaApi, document);
    }

    protected AnnotatedDocument request(AnnotatedDocument document) {
        // give the document a URI
        document.setDocumentURI(DOCUMENT_URI + documentCount);
        ++documentCount;
        // create NIF document
        String nifDocument = nifCreator.getDocumentAsNIFString(document);
        HttpEntity entity = null;
        try {
            entity = new StringEntity(nifDocument, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception while creating POST request.", e);
            throw new AnnotationException("Exception while creating POST request. "
                    + e.getLocalizedMessage());
        }
        // send NIF document (start time measure)
        lastRequestSend = System.currentTimeMillis();
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
                throw new AnnotationException("Exception while sending request. "
                        + e.getLocalizedMessage());
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.error("Response has the wrong status: " + status.toString());
                throw new AnnotationException("Response has the wrong status: " + status.toString());
            }
            // receive NIF document (end time measure and set time)
            entity = response.getEntity();
            lastResponseReceived = System.currentTimeMillis();
            // read response and parse NIF
            try {
                reader = new InputStreamReader(entity.getContent());
                document = nifParser.getDocumentFromNIFReader(reader);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response.", e);
                throw new AnnotationException("Couldn't parse the response. "
                        + e.getLocalizedMessage());
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
        return document;
    }
}
