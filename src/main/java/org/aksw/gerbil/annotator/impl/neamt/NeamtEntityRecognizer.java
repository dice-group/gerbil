package org.aksw.gerbil.annotator.impl.neamt;

import java.util.List;

import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;

public class NeamtEntityRecognizer extends AbstractNeamtAnnotator implements EntityRecognizer {

    public NeamtEntityRecognizer(String serviceUrl, String components, String lang) {
        super(serviceUrl, components, lang);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return request(document).getMarkings(Span.class);
    }

}
