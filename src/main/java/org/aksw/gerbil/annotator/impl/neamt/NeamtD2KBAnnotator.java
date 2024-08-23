package org.aksw.gerbil.annotator.impl.neamt;

import java.util.List;

import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class NeamtD2KBAnnotator extends AbstractNeamtAnnotator implements D2KBAnnotator {

    public NeamtD2KBAnnotator(String serviceUrl, String components, String lang) {
        super(serviceUrl, components, lang);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return request(document).getMarkings(MeaningSpan.class);
    }

    @Override
    protected JsonObject createRequestBody(Document document) {
        // Add the entity mentions to the request
        String text = document.getText();
        JsonObject requestBody = super.createRequestBody(document);
        JsonArray mentions = new JsonArray();
        int start;
        int end;
        for (Span span : document.getMarkings(Span.class)) {
            start = span.getStartPosition();
            end = start + span.getLength();
            JsonObject mention = new JsonObject();
            mention.addProperty("start", start);
            mention.addProperty("end", end);
            mention.addProperty("surface_form", text.substring(start, end));
            mentions.add(mention);
        }
        requestBody.add("ent_mentions", mentions);
        return requestBody;
    }
}
