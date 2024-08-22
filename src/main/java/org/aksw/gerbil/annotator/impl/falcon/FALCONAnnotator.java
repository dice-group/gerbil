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
package org.aksw.gerbil.annotator.impl.falcon;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.Annotation;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FALCONAnnotator extends AbstractHttpBasedAnnotator implements A2KBAnnotator, C2KBAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FALCONAnnotator.class);

    private static final Charset CHARSET = Charset.forName("utf-8");
    private static final ContentType REQUEST_CONTENT_TYPE = ContentType.create("application/json", Consts.UTF_8);
    private static final String TEXT_REQUEST_PARAMETER_KEY = "text";

    private String serviceUrl;

    public FALCONAnnotator(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    protected Document requestAnnotations(Document doc, boolean withPositions) throws GerbilException {
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
        parameters.addProperty(TEXT_REQUEST_PARAMETER_KEY, text);
        request.setEntity(new StringEntity(parameters.toString(), CHARSET));
        request.addHeader(HttpHeaders.CONTENT_TYPE, REQUEST_CONTENT_TYPE.toString());
        request.addHeader(HttpHeaders.ACCEPT, REQUEST_CONTENT_TYPE.toString());

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Document resultDoc = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            try {
                resultDoc = new DocumentImpl(text, documentUri);
                String content = IOUtils.toString(entity.getContent());
                JsonObject outJson = new JsonParser().parse(content).getAsJsonObject();
                parseMarkings(outJson, resultDoc, withPositions);
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
        LOGGER.info("Finished request for {}", resultDoc.getDocumentURI());
        return resultDoc;
    }

    protected void parseMarkings(JsonObject out, Document resultDoc, boolean withPositions) {
        List<String[]> dbpediaEntities = getIriStringPairs(out, "entities_dbpedia");
        List<String[]> wikidataEntities = getIriStringPairs(out, "entities_wikidata");
        if (withPositions) {
            createMergedAnnotations(resultDoc, dbpediaEntities, wikidataEntities);
        } else {
            createMergedAnnotations(resultDoc, dbpediaEntities, wikidataEntities);
        }
    }

    protected List<String[]> getIriStringPairs(JsonObject out, String annotationName) {
        if (out.has(annotationName) && out.get(annotationName).isJsonArray()) {
            return getIriStringPairs(out.get(annotationName).getAsJsonArray());
        } else {
            LOGGER.warn("Didn't find any {} annotations.", annotationName);
            return Collections.emptyList();
        }
    }

    protected List<String[]> getIriStringPairs(JsonArray entities) {
        List<String[]> iriSurfaceFormPairs = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).isJsonObject()) {
                JsonObject entity = entities.get(i).getAsJsonObject();
                if (entity.has("URI") && entity.has("surface form")) {
                    iriSurfaceFormPairs.add(
                            new String[] { entity.get("URI").getAsString(), entity.get("surface form").getAsString() });
                } else {
                    LOGGER.warn("Couldn't parse the following entity in the response: {}", entity);
                }
            } else {
                LOGGER.warn("Couldn't parse the following element of the response: {}", entities.get(i));
            }
        }
        return iriSurfaceFormPairs;
    }

    protected void createMergedAnnotations(Document resultDoc, List<String[]> dbpediaEntities,
            List<String[]> wikidataEntities) {
        Map<String, Set<String>> surface2Iris = Stream.concat(dbpediaEntities.stream(), wikidataEntities.stream())
                .collect(Collectors.groupingBy(p -> p[1], Collectors.mapping(p -> p[0], Collectors.toSet())));
        for (Entry<String, Set<String>> e : surface2Iris.entrySet()) {
            resultDoc.addMarking(new Annotation(e.getValue()));
        }
    }

    protected void createMergedNamedEntities(Document resultDoc, List<String[]> dbpediaEntities,
            List<String[]> wikidataEntities) {
        String text = resultDoc.getText();
        Iterator<String[]> dbpPairIter = dbpediaEntities.iterator();
        Iterator<String[]> wikiPairIter = wikidataEntities.iterator();
        NamedEntity dbpEntity = dbpPairIter.hasNext() ? getNextAnnotation(dbpPairIter.next(), text, 0) : null;
        NamedEntity wikiEntity = wikiPairIter.hasNext() ? getNextAnnotation(wikiPairIter.next(), text, 0) : null;
        int dbpPos = 0;
        int wikiPos = 0;
        boolean moveDBp = false;
        boolean moveWiki = false;
        // Go through the lists of annotations, find them in the text, and merge them if
        // they mark exactly the same position
        while (dbpEntity != null || wikiEntity != null) {
            dbpPos = (dbpEntity != null) ? dbpEntity.getStartPosition() : Integer.MAX_VALUE;
            wikiPos = (wikiEntity != null) ? wikiEntity.getStartPosition() : Integer.MAX_VALUE;
            if (dbpPos < wikiPos) {
                // The DBpedia entity is the next one in the text
                resultDoc.addMarking(dbpEntity);
                moveDBp = true;
            } else if (wikiPos < dbpPos) {
                // The Wikidata entity is the next one in the text
                resultDoc.addMarking(wikiEntity);
                moveWiki = true;
            } else if (dbpPos == wikiPos) {
                // Both annotations start at the same position
                if (dbpEntity.getLength() == wikiEntity.getLength()) {
                    // They mark exactly the same text --> they are the same entity
                    dbpEntity.getUris().addAll(wikiEntity.getUris());
                    resultDoc.addMarking(dbpEntity);
                } else {
                    // Their length is different, so add both as separate entity
                    resultDoc.addMarking(dbpEntity);
                    resultDoc.addMarking(wikiEntity);
                }
                moveDBp = true;
                moveWiki = true;
            }
            // Get next entity
            if (moveDBp) {
                dbpEntity = dbpPairIter.hasNext()
                        ? getNextAnnotation(dbpPairIter.next(), text,
                                text.substring(dbpEntity.getStartPosition(),
                                        dbpEntity.getStartPosition() + dbpEntity.getLength()),
                                dbpEntity.getStartPosition())
                        : null;
                moveDBp = false;
            }
            if (moveWiki) {
                wikiEntity = wikiPairIter.hasNext()
                        ? getNextAnnotation(wikiPairIter.next(), text,
                                text.substring(wikiEntity.getStartPosition(),
                                        wikiEntity.getStartPosition() + wikiEntity.getLength()),
                                wikiEntity.getStartPosition())
                        : null;
                moveWiki = false;
            }
        }
    }

    protected NamedEntity getNextAnnotation(String[] pair, String text, String previousSurfaceForm, int startPos) {
        // If the surface form equal each other, we have to ensure that we won't get the
        // same position again
        int pos = startPos;
        if (previousSurfaceForm.equalsIgnoreCase(pair[1])) {
            ++pos;
        }
        return getNextAnnotation(pair, text, pos);
    }

    protected NamedEntity getNextAnnotation(String[] pair, String text, int startPos) {
        int pos = text.indexOf(pair[1], startPos);
        if (pos < 0) {
            // Let's try it again with a lower-cased variant of text and surface form
            pos = text.toLowerCase().indexOf(pair[1].toLowerCase(), startPos);
        }
        if (pos < 0) {
            LOGGER.error(
                    "Couldn't find the annotation \"{}\" from position {} onwards in the text \"{}\". It will be ignored.",
                    pair[1], startPos, text);
            return null;
        } else {
            return new NamedEntity(pos, pair[1].length(), pair[0]);
        }
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        // FIXME merge annotations that have the same meaning but different positions
        return requestAnnotations(document, false).getMarkings(Meaning.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document, true).getMarkings(MeaningSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return requestAnnotations(document, true).getMarkings(Span.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return requestAnnotations(document, true).getMarkings(MeaningSpan.class);
    }
}