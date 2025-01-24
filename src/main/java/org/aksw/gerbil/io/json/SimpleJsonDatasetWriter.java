package org.aksw.gerbil.io.json;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SimpleJsonDatasetWriter {

    protected static final BiFunction<Document, Integer, Integer> DEFAULT_ID_GENERATOR = (d, i) -> i;

    public void writeDocuments(List<Document> documents, File file) throws IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeDocuments(documents, out);
        }
    }

    public void writeDocuments(List<Document> documents, OutputStream out) throws IOException {
        writeDocuments(documents, DEFAULT_ID_GENERATOR, out);
    }

    public void writeDocuments(List<Document> documents, BiFunction<Document, Integer, Integer> idGen, OutputStream out)
            throws IOException {
        IOUtils.write(writeDocuments(documents, idGen), out, StandardCharsets.UTF_8);
    }

    public String writeDocuments(List<Document> documents) throws IOException {
        return writeDocuments(documents, DEFAULT_ID_GENERATOR);
    }

    public String writeDocuments(List<Document> documents, BiFunction<Document, Integer, Integer> idGen)
            throws IOException {
        return documents2Json(documents, idGen).toString();
    }

    protected JsonElement documents2Json(List<Document> documents, BiFunction<Document, Integer, Integer> idGen) {
        // Ensure that the ID generator is not null
        BiFunction<Document, Integer, Integer> idGenerator = idGen == null ? DEFAULT_ID_GENERATOR : idGen;
        JsonArray dArray = new JsonArray();
        int id = 0;
        for (Document document : documents) {
            dArray.add(document2Json(document, idGenerator.apply(document, id)));
            ++id;
        }
        return dArray;
    }

    protected JsonElement document2Json(Document document, int id) {
        JsonObject dObject = new JsonObject();
        dObject.addProperty("id", id);
        dObject.addProperty("iri", document.getDocumentURI());
        dObject.addProperty("text", document.getText());
        dObject.add("annotations", markings2Json(document.getMarkings()));
        return dObject;
    }

    protected JsonElement markings2Json(List<Marking> markings) {
        JsonArray mArray = new JsonArray();
        for (Marking marking : markings) {
            mArray.add(marking2Json(marking));
        }
        return mArray;
    }

    protected JsonElement marking2Json(Marking marking) {
        JsonObject mObject = new JsonObject();
        if (marking instanceof Span) {
            int start = ((Span) marking).getStartPosition();
            mObject.addProperty("start", start);
            mObject.addProperty("end", start + ((Span) marking).getLength());
        }
        if (marking instanceof ScoredMarking) {
            mObject.addProperty("confidence", ((ScoredMarking) marking).getConfidence());
        }
        if (marking instanceof Meaning) {
            mObject.add("meaning", strings2JsonArray(((Meaning) marking).getUris()));
        }
        if (marking instanceof TypedMarking) {
            mObject.add("types", strings2JsonArray(((TypedMarking) marking).getTypes()));
        }
        return mObject;
    }

    protected JsonElement strings2JsonArray(Collection<String> strings) {
        final JsonArray sArray = new JsonArray();
        strings.stream().map(iri -> new JsonPrimitive(iri)).forEach(iri -> sArray.add(iri));
        return sArray;
    }
}
