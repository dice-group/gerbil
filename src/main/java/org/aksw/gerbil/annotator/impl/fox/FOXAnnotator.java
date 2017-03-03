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
package org.aksw.gerbil.annotator.impl.fox;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FOXAnnotator extends AbstractHttpBasedAnnotator implements OKETask1Annotator {

    private static final String FOX_SERVICE_URL_PARAMETER_KEY = "org.aksw.gerbil.annotators.FOXAnnotatorConfig.serviceUrl";

    private static final Logger LOGGER = LoggerFactory.getLogger(FOXAnnotator.class);

    private static final String PERSON_TYPE_URI = "scmsann:PERSON";
    private static final String LOCATION_TYPE_URI = "scmsann:LOCATION";
    private static final String ORGANIZATION_TYPE_URI = "scmsann:ORGANIZATION";
    private static final String DOLCE_PERSON_TYPE_URI = "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person";
    private static final String DOLCE_LOCATION_TYPE_URI = "http://www.ontologydesignpatterns.org/ont/d0.owl#Location";
    private static final String DOLCE_ORGANIZATION_TYPE_URI = "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization";

    @Deprecated
    public static final String NAME = "FOX";

    private String serviceUrl;

    public FOXAnnotator() throws GerbilException {
        serviceUrl = GerbilConfiguration.getInstance().getString(FOX_SERVICE_URL_PARAMETER_KEY);
        if (serviceUrl == null) {
            throw new GerbilException("Couldn't load the needed property \"" + FOX_SERVICE_URL_PARAMETER_KEY + "\".",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
        }
    }

    public FOXAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(Span.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedSpan.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedNamedEntity.class);
    }

    @Override
    public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(TypedSpan.class);
    }

    protected Document requestAnnotations(Document document) throws GerbilException {
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());
        HttpEntity entity = new StringEntity(new JSONObject().put("input", document.getText()).put("type", "text")
                .put("task", "ner").put("output", "JSON-LD").toString(), ContentType.APPLICATION_JSON);
        // request FOX
        HttpPost request = null;
        try {
            request = createPostRequest(serviceUrl);
        } catch (IllegalArgumentException e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        // FOX does not like the Accept header. So which should avoid it.
        // request.addHeader(HttpHeaders.ACCEPT,
        // ContentType.APPLICATION_JSON.getMimeType());
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, ContentType.APPLICATION_JSON.getCharset().name());
        request.setEntity(entity);

        entity = null;
        CloseableHttpResponse response = null;
        try {
            response = sendRequest(request);

            entity = response.getEntity();
            try {
                String content = IOUtils.toString(entity.getContent(),
                        ContentType.APPLICATION_JSON.getCharset().name());
                // parse results
                JSONObject outObj = new JSONObject(content);
                if (outObj.has("@graph")) {

                    JSONArray graph = outObj.getJSONArray("@graph");
                    for (int i = 0; i < graph.length(); i++) {
                        parseType(graph.getJSONObject(i), resultDoc);
                    }
                } else {
                    parseType(outObj, resultDoc);
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

    protected void parseType(JSONObject entity, Document resultDoc) throws GerbilException {
        try {

            if (entity != null && entity.has("means") && entity.has("beginIndex") && entity.has("ann:body")) {

                String uri = entity.getString("means");
                if (uri.startsWith("dbpedia:")) {
                    uri = "http://dbpedia.org/resource/" + uri.substring(8);
                }
                String body = entity.getString("ann:body");
                Object begin = entity.get("beginIndex");
                Object typeObject = entity.get("@type");
                Set<String> types = new HashSet<String>();
                if (typeObject instanceof JSONArray) {
                    JSONArray typeArray = (JSONArray) typeObject;
                    for (int i = 0; i < typeArray.length(); ++i) {
                        addType(typeArray.getString(i), types);
                    }
                } else {
                    addType(typeObject.toString(), types);
                }
                uri = URLDecoder.decode(uri, "UTF-8");
                if (begin instanceof JSONArray) {
                    // for all indices
                    for (int i = 0; i < ((JSONArray) begin).length(); ++i) {
                        resultDoc.addMarking(new TypedNamedEntity(Integer.valueOf(((JSONArray) begin).getString(i)),
                                body.length(), uri, types));
                    }
                } else if (begin instanceof String) {
                    resultDoc.addMarking(
                            new TypedNamedEntity(Integer.valueOf((String) begin), body.length(), uri, types));
                } else if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Couldn't find start position for annotation.");
                }
            }
        } catch (Exception e) {
            throw new GerbilException("Got an Exception while parsing the response of FOX.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
    }

    protected void addType(String typeString, Set<String> types) {
        switch (typeString) {
        case PERSON_TYPE_URI: {
            types.add(DOLCE_PERSON_TYPE_URI);
            break;
        }
        case LOCATION_TYPE_URI: {
            types.add(DOLCE_LOCATION_TYPE_URI);
            break;
        }
        case ORGANIZATION_TYPE_URI: {
            types.add(DOLCE_ORGANIZATION_TYPE_URI);
            break;
        }
        }
        types.add(typeString.replaceFirst("scmsann:", "http://ns.aksw.org/scms/annotations/"));
    }
}
