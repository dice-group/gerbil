package org.aksw.gerbil.io.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MarkingBuilder;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleJsonDatasetReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJsonDatasetReader.class);

    public List<Document> readDocuments(String data) {
        return readDocuments(data, "");
    }

    public List<Document> readDocuments(String data, String docIriNamespace) {
        JsonElement parsedData = new JsonParser().parse(data);
        if (parsedData.isJsonArray()) {
            return parseDocuments(parsedData.getAsJsonArray(), docIriNamespace);
        } else {
            LOGGER.warn("Expected a JSON array containing documents but couldn't find one. Returning null.");
        }
        return null;
    }

    public List<Document> parseDocuments(JsonArray dArray, String docIriNamespace) {
        List<Document> documents = new ArrayList<>();
        int defaultId = 0;
        for (JsonElement dObject : dArray) {
            documents.add(parseDocument(dObject.getAsJsonObject(), docIriNamespace, defaultId));
            ++defaultId;
        }
        return documents;
    }

    public Document parseDocument(JsonObject dObject, String docIriNamespace, int defaultId) {
        String text = dObject.has("text") ? dObject.get("text").getAsString() : "";
        String iri = null;
        if (dObject.has("iri")) {
            iri = dObject.get("iri").getAsString();
        }
        if (iri == null) {
            int id = defaultId;
            if (dObject.has("id")) {
                id = dObject.get("id").getAsInt();
            }
            iri = docIriNamespace + id;
        }
        Document document = new DocumentImpl(text, iri);
        if (dObject.has("annotations")) {
            parseMarkings(dObject.get("annotations").getAsJsonArray(), document.getMarkings());
        }
        return document;
    }

    protected void parseMarkings(JsonArray mArray, List<Marking> markings) {
        MarkingBuilder builder = new MarkingBuilder();
        Marking marking = null;
        for (JsonElement mObject : mArray) {
            if (mObject.isJsonObject()) {
                builder.clear();
                parseMarking(mObject.getAsJsonObject(), builder);
                marking = builder.build();
                if (marking != null) {
                    markings.add(marking);
                }
            }
        }
    }

    protected void parseMarking(JsonObject mObject, MarkingBuilder builder) {
        if (mObject.has("start")) {
            builder.setStart(mObject.get("start").getAsInt());
        }
        if (mObject.has("end")) {
            builder.setEnd(mObject.get("end").getAsInt());
        }
        if (mObject.has("confidence")) {
            builder.setConfidence(mObject.get("confidence").getAsDouble());
        }
        if (mObject.has("meaning")) {
            builder.setMeanings(getAsStringSet(mObject.get("meaning").getAsJsonArray()));
        }
        if (mObject.has("types")) {
            builder.setTypes(getAsStringSet(mObject.get("types").getAsJsonArray()));
        }
    }

    protected Set<String> getAsStringSet(JsonArray sArray) {
        Set<String> set = new HashSet<>();
        sArray.forEach(s -> set.add(s.getAsString()));
        return set;
    }
}
