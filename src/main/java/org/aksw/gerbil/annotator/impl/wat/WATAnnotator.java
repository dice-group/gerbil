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
package org.aksw.gerbil.annotator.impl.wat;

import java.io.IOException;
import java.util.List;

import org.aksw.gerbil.annotator.EntityExtractor;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.utils.Wikipedia2DBPediaTransformer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WATAnnotator extends AbstractHttpBasedAnnotator implements EntityExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WATAnnotator.class);

    private static final String WAT_CONFIG_FILE_PROPERTY_ENDPOINT = "org.aksw.gerbil.annotators.wat.disambiguateUrl";
    private static final String WAT_CONFIG_FILE_PROPERTY_PARAMETERS = "org.aksw.gerbil.annotators.wat.annotateUrl";

    private static final String SPANS_REQUEST_PARAMETER_KEY = "spans";
    private static final String TEXT_REQUEST_PARAMETER_KEY = "text";
    private static final String ENTITY_TITLE_KEY = "title";
    private static final String ENTITY_CONFIDENCE_KEY = "rho";
    private static final String ENTITY_START_KEY = "start";
    private static final String ENTITY_END_KEY = "end";

    private final String annotateUrl;
    private final String disambiguateUrl;

    public WATAnnotator() throws GerbilException {
        this.annotateUrl = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_PARAMETERS);
        if (annotateUrl == null) {
            throw new GerbilException("Couldn't load parameters (\"" + WAT_CONFIG_FILE_PROPERTY_PARAMETERS + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
        this.disambiguateUrl = GerbilConfiguration.getInstance().getString(WAT_CONFIG_FILE_PROPERTY_ENDPOINT);
        if (disambiguateUrl == null) {
            throw new GerbilException("Couldn't load endpoint (\"" + WAT_CONFIG_FILE_PROPERTY_ENDPOINT + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
    }

    public WATAnnotator(String annotateUrl, String disambiguateUrl) {
        this.disambiguateUrl = disambiguateUrl;
        this.annotateUrl = annotateUrl;
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        return requestAnnotations(document, true).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(Span.class);
    }

    private Document requestAnnotations(Document document, boolean disambiguate) throws GerbilException {
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());
        JSONObject parameters = new JSONObject();
        if (disambiguate) {
            JSONArray mentionsJson = new JSONArray();
            for (Span span : document.getMarkings(Span.class)) {
                JSONObject mentionJson = new JSONObject();
                mentionJson.put(ENTITY_START_KEY, span.getStartPosition());
                mentionJson.put(ENTITY_END_KEY, span.getStartPosition() + span.getLength());
                mentionsJson.put(mentionJson);
            }
            parameters.put(SPANS_REQUEST_PARAMETER_KEY, mentionsJson);
        }
        parameters.put(TEXT_REQUEST_PARAMETER_KEY, document.getText());

        HttpPost request = createPostRequest(disambiguate ? disambiguateUrl : annotateUrl);
        request.setEntity(new StringEntity(parameters.toString(), "UTF8"));
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                String content = IOUtils.toString(entity.getContent(), "UTF-8");
                // parse results
                JSONObject outObj = new JSONObject(content);
                JSONArray jsAnnotations = outObj.getJSONArray("annotations");
                int start;
                for (int i = 0; i < jsAnnotations.length(); i++) {
                    JSONObject js_ann = jsAnnotations.getJSONObject(i);
                    if (js_ann.has(ENTITY_START_KEY) && js_ann.has(ENTITY_END_KEY) && js_ann.has(ENTITY_TITLE_KEY)) {
                        start = js_ann.getInt(ENTITY_START_KEY);
                        if (js_ann.has(ENTITY_CONFIDENCE_KEY)) {
                            resultDoc.addMarking(new ScoredNamedEntity(start, js_ann.getInt(ENTITY_END_KEY) - start,
                                    Wikipedia2DBPediaTransformer.generateUriSet(js_ann.getString(ENTITY_TITLE_KEY)),
                                    js_ann.getDouble(ENTITY_CONFIDENCE_KEY)));
                        } else {
                            resultDoc.addMarking(new NamedEntity(start, js_ann.getInt(ENTITY_END_KEY) - start,
                                    Wikipedia2DBPediaTransformer.generateUriSet(js_ann.getString(ENTITY_TITLE_KEY))));
                        }
                    }
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
}
