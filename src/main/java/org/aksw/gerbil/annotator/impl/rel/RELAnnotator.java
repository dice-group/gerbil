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
package org.aksw.gerbil.annotator.impl.rel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RELAnnotator extends AbstractHttpBasedAnnotator implements A2KBAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RELAnnotator.class);

    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final ContentType REQUEST_CONTENT_TYPE = ContentType.create("application/json", Consts.UTF_8);

    private static final String TEXT_REQUEST_PARAMETER_KEY = "text";
    private static final String SPANS_REQUEST_PARAMETER_KEY = "spans";

    private String serviceUrl;

    public RELAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected Document requestAnnotations(Document doc, boolean isD2KB) throws GerbilException {
        String text = doc.getText();
        String documentUri = doc.getDocumentURI();
        LOGGER.info("Started request for {}", documentUri);
        HttpPost request = null;
        try {
            request = createPostRequest(serviceUrl);
        } catch (Exception e) {
            throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }

        JsonObject parameters = new JsonObject();
        JsonArray spans = new JsonArray();
        parameters.addProperty(TEXT_REQUEST_PARAMETER_KEY, text);
        parameters.add(SPANS_REQUEST_PARAMETER_KEY, spans);
        if(isD2KB) {
            for(Span s: doc.getMarkings(Span.class)){
                JsonObject span = new JsonObject();
                span.addProperty("start", s.getStartPosition());
                span.addProperty("length", s.getLength());
                spans.add(span);
            }
        }
        
        request.setEntity(new StringEntity(parameters.toString(), CHARSET));
        request.addHeader(HttpHeaders.CONTENT_TYPE, REQUEST_CONTENT_TYPE.toString());
        request.addHeader(HttpHeaders.ACCEPT, REQUEST_CONTENT_TYPE.toString());
        
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Document document = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                document = new DocumentImpl(text, documentUri);
                JsonArray outArray = new JsonParser().parse(IOUtils.toString(entity.getContent())).getAsJsonArray();
                if(outArray != null) {
                    parseMarkings(outArray, document);
                }
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
        LOGGER.info("Finished request for {}", documentUri);
        return document; 
    }

    private void parseMarkings(JsonArray outArray, Document document){
        for(JsonElement element: outArray) {
            JsonArray entity = element.getAsJsonArray();
            int start = entity.get(0).getAsInt();
            int length = entity.get(1).getAsInt();
            String uri = "http://dbpedia.org/resource/" + entity.get(3).getAsString();
            document.addMarking(new NamedEntity(start, length, new HashSet<String>(Arrays.asList(uri))));
        } 
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document, true).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document, false).getMarkings(Span.class);
    }
}
