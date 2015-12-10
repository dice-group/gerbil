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
package org.aksw.gerbil.annotator.impl.wikiminer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.utils.WikipediaHelper;
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

/**
 * Client of the WikipediaMiner service
 * (http://wikipediadataminer.cms.waikato.ac.nz/).
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class WikipediaMinerAnnotator extends AbstractHttpBasedAnnotator implements A2KBAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikipediaMinerAnnotator.class);

    private static final String DETECTED_ENTITIES_LIST_KEY = "detectedTopics";
    private static final String DETECTED_ENTITY_TITLE_KEY = "title";
    private static final String DETECTED_ENTITY_WEIGHT_KEY = "weight";
    private static final String DETECTED_ENTITY_POSITIONS_KEY = "references";
    private static final String DETECTED_ENTITY_START_KEY = "start";
    private static final String DETECTED_ENTITY_END_KEY = "end";

    /**
     * Parameters of WikiMiner. The format and reference parameters are
     * mandatory to parse the result. The String has to end with the source
     * parameter at which the text will be appended.
     * 
     * repeatModer:
     * "whether repeat mentions of topics should be tagged or ignored"
     * 
     * minProbability: "The system calculates a probability for each topic of
     * whether a Wikipedian would consider it interesting enough to link to.
     * This parameter specifies the minimum probability a topic must have before
     * it will be linked."
     */
    private static final String PARAMETERS = "responseFormat=json&references=true&repeatMode=all&minProbability=0.0&source=";

    protected String serviceUrl;

    public WikipediaMinerAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(Meaning.class);
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
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document).getMarkings(MeaningSpan.class);
    }

    protected Document requestAnnotations(Document document) throws GerbilException {
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());
        HttpPost request = null;
        try {
            request = createPostRequest(serviceUrl);
        } catch (IllegalArgumentException e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        String parameters = null;
        try {
            parameters = PARAMETERS + URLEncoder.encode(document.getText(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Couldn't encode request data.", e);
            throw new GerbilException("Couldn't encode request data.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        HttpEntity entity = new StringEntity(parameters, ContentType.APPLICATION_FORM_URLENCODED);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        request.setEntity(entity);

        entity = null;
        CloseableHttpResponse response = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                parseJson(IOUtils.toString(entity.getContent(), "UTF-8"), resultDoc);
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

    private void parseJson(String content, Document resultDoc) {
        // parse results
        JSONObject outObj = new JSONObject(content);
        if (outObj.has(DETECTED_ENTITIES_LIST_KEY)) {
            JSONArray entities = outObj.getJSONArray(DETECTED_ENTITIES_LIST_KEY);
            for (int i = 0; i < entities.length(); i++) {
                parseEntity(entities.getJSONObject(i), resultDoc);
            }
        }
    }

    private void parseEntity(JSONObject entityObject, Document resultDoc) {
        if (entityObject.has(DETECTED_ENTITY_TITLE_KEY) && entityObject.has(DETECTED_ENTITY_WEIGHT_KEY)
                && entityObject.has(DETECTED_ENTITY_POSITIONS_KEY)) {
            Set<String> uris = WikipediaHelper.generateUriSet(entityObject.getString(DETECTED_ENTITY_TITLE_KEY));
            double probability = entityObject.getDouble(DETECTED_ENTITY_WEIGHT_KEY);
            JSONArray positions = entityObject.getJSONArray(DETECTED_ENTITY_POSITIONS_KEY);
            JSONObject position;
            int start, end;
            for (int i = 0; i < positions.length(); i++) {
                position = positions.getJSONObject(i);
                if (position.has(DETECTED_ENTITY_START_KEY) && position.has(DETECTED_ENTITY_END_KEY)) {
                    start = position.getInt(DETECTED_ENTITY_START_KEY);
                    end = position.getInt(DETECTED_ENTITY_END_KEY);
                    resultDoc.addMarking(new ScoredNamedEntity(start, end - start, uris, probability));
                }
            }
        }
    }

}
