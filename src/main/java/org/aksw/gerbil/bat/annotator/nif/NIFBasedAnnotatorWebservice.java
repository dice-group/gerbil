package org.aksw.gerbil.bat.annotator.nif;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.utils.AnnotationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.aksw.gerbil.transfer.nif.AnnotatedDocument;
import org.aksw.gerbil.transfer.nif.NIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIFBasedAnnotatorWebservice implements D2WSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFBasedAnnotatorWebservice.class);

    private static final String DOCUMENT_URI = "http://www.aksw.org/gerbil/NifWebService/request_";

    private String url;
    private String name;
    private HttpClient client;
    private long lastRequestSend = 0;
    private long lastResponseReceived = 0;
    private int documentCount = 0;
    private NIFDocumentCreator nifCreator; // FIXME set this
    private NIFDocumentParser nifParser; // FIXME set this

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
        // give the document a URI
        document.setDocumentURI(DOCUMENT_URI + documentCount);
        // create NIF document
        String nifDocument = nifCreator.getDocumentAsNIFString(document);
        HttpEntity entity = null;
        try {
            entity = new StringEntity(nifDocument, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // LOGGER.entity = new StringEntity(nifDocument, "UTF-8");
            // TODO
        }
        // TODO send NIF document (start time measure)
        lastRequestSend = System.currentTimeMillis();
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);
        request.addHeader("Content-Type", nifCreator.getHttpContentType());
        request.addHeader("Accept", nifParser.getHttpContentType());

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        entity = response.getEntity();
        // TODO receive NIF document (end time measure and set time)
        lastResponseReceived = System.currentTimeMillis();
        // TODO read response and set nifDocument
        // TODO parse NIF response
        try {
            document = nifParser.getDocumentFromNIFString(nifDocument);
        } catch (Exception e) {
            throw new AnnotationException("Couldn't parse the response. "
                    + e.getLocalizedMessage());
        }
        // translate the annotated document into a HashSet of BAT Annotations
        return BAT2NIF_TranslationHelper.createAnnotations(document);
    }
}
