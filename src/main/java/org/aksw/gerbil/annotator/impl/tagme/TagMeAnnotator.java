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
package org.aksw.gerbil.annotator.impl.tagme;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.http.HttpManagement;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredSpanImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagMeAnnotator extends AbstractHttpBasedAnnotator implements A2KBAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagMeAnnotator.class);

    private static final String TAGME_KEY_PARAMETER_KEY = "org.aksw.gerbil.annotators.TagMe.key";
    private static final String KEY_STORE_RESOURCE_NAME = "TagMe_keyStore.jks";
    private static final char KEY_STORE_PASSWORD[] = "tagme2".toCharArray();

    private static final String ANNOTATIONS_LIST_KEY = "annotations";
    private static final String ANNOTATION_TITLE_KEY = "title";
    private static final String SPOTTINGS_LIST_KEY = "spots";
    private static final String LINK_PROBABILITY_KEY = "lp";
    private static final String ANNOTATION_GOODNESS_KEY = "rho";
    private static final String START_KEY = "start";
    private static final String END_KEY = "end";

    private String annotationUrl;
    private String spotUrl;
    private String key;

    public TagMeAnnotator(String annotationUrl, String spotUrl) throws GerbilException {
        this.annotationUrl = annotationUrl;
        this.spotUrl = spotUrl;
        key = GerbilConfiguration.getInstance().getString(TAGME_KEY_PARAMETER_KEY);
        if (key == null) {
            throw new GerbilException("Couldn't load key from configuration (\"" + TAGME_KEY_PARAMETER_KEY + "\").",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        init();
    }

    public TagMeAnnotator(String annotationUrl, String spotUrl, String key) throws GerbilException {
        this.annotationUrl = annotationUrl;
        this.spotUrl = spotUrl;
        this.key = key;
        init();
    }

    protected void init() throws GerbilException {
        HttpClientBuilder builder = HttpManagement.getInstance().generateHttpClientBuilder();
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream instream = this.getClass().getClassLoader().getResourceAsStream(KEY_STORE_RESOURCE_NAME);
            try {
                keyStore.load(instream, KEY_STORE_PASSWORD);
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                    .build();
            builder.setSSLContext(sslcontext);

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" },
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            builder.setSSLSocketFactory(sslsf);
            CloseableHttpClient localClient = builder.build();
            this.setClient(localClient);
        } catch (Exception e) {
            throw new GerbilException("Couldn't initialize SSL context.", e, ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        this.setClient(builder.build());
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return performRequest(document, true).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return performRequest(document, true).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return performRequest(document, false).getMarkings(Span.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return performRequest(document, true).getMarkings(MeaningSpan.class);
    }

    protected Document performRequest(Document document, boolean annotate) throws GerbilException {
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());

        HttpPost request = null;
        try {
            request = createPostRequest(annotate ? annotationUrl : spotUrl);
        } catch (IllegalArgumentException e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }

        StringBuilder parameters = new StringBuilder();
        parameters.append("lang=en&gcube-token=");
        parameters.append(key);
        parameters.append("&text=");
        try {
            parameters.append(URLEncoder.encode(document.getText(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Couldn't encode request data.", e);
            throw new GerbilException("Couldn't encode request data.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        HttpEntity entity = new StringEntity(parameters.toString(), ContentType.APPLICATION_FORM_URLENCODED);
        request.addHeader("accept", "application/json");
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("charset", "utf-8");
        request.setEntity(entity);

        entity = null;
        CloseableHttpResponse response = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                if (annotate) {
                    parseAnnotations(IOUtils.toString(entity.getContent(), "UTF-8"), resultDoc);
                } else {
                    parseSpottings(IOUtils.toString(entity.getContent(), "UTF-8"), resultDoc);
                }
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
        return resultDoc;
    }

    private String transformTitleToUri(String title) {
        return "http://dbpedia.org/resource/" + title.replace(' ', '_');
    }

    private void parseAnnotations(String content, Document resultDoc) {
        // parse results
        JSONObject outObj = new JSONObject(content);
        if (outObj.has(ANNOTATIONS_LIST_KEY)) {
            JSONArray entities = outObj.getJSONArray(ANNOTATIONS_LIST_KEY);
            for (int i = 0; i < entities.length(); i++) {
                parseAnnotation(entities.getJSONObject(i), resultDoc);
            }
        }
    }

    private void parseAnnotation(JSONObject entityObject, Document resultDoc) {
        if (entityObject.has(ANNOTATION_TITLE_KEY) && entityObject.has(START_KEY) && entityObject.has(END_KEY)) {
            String uri = transformTitleToUri(entityObject.getString(ANNOTATION_TITLE_KEY));
            int start = entityObject.getInt(START_KEY);
            int end = entityObject.getInt(END_KEY);
            if (entityObject.has(ANNOTATION_GOODNESS_KEY)) {
                resultDoc.addMarking(new ScoredNamedEntity(start, end - start, uri,
                        entityObject.getDouble(ANNOTATION_GOODNESS_KEY)));
            } else {
                resultDoc.addMarking(new NamedEntity(start, end - start, uri));
            }
        }
    }

    private void parseSpottings(String content, Document resultDoc) {
        // parse results
        JSONObject outObj = new JSONObject(content);
        if (outObj.has(SPOTTINGS_LIST_KEY)) {
            JSONArray entities = outObj.getJSONArray(SPOTTINGS_LIST_KEY);
            for (int i = 0; i < entities.length(); i++) {
                parseSpotting(entities.getJSONObject(i), resultDoc);
            }
        }
    }

    private void parseSpotting(JSONObject entityObject, Document resultDoc) {
        if (entityObject.has(START_KEY) && entityObject.has(END_KEY)) {
            int start = entityObject.getInt(START_KEY);
            int end = entityObject.getInt(END_KEY);
            if (entityObject.has(LINK_PROBABILITY_KEY)) {
                resultDoc.addMarking(
                        new ScoredSpanImpl(start, end - start, entityObject.getDouble(LINK_PROBABILITY_KEY)));
            } else {
                resultDoc.addMarking(new SpanImpl(start, end - start));
            }
        }
    }

    @Override
    protected void performClose() throws IOException {
        client.close();
        super.performClose();
    }
}
