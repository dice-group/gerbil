package org.aksw.gerbil.annotator.impl.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

public class InstanceListBasedAnnotator extends AbstractAnnotator implements A2KBAnnotator, C2KBAnnotator,
        D2KBAnnotator, EntityRecognizer, EntityTyper, OKETask1Annotator, OKETask2Annotator {

    protected Map<String, Document> uriInstanceMapping;

    public InstanceListBasedAnnotator(String annotatorName, List<Document> instances) {
        super(annotatorName);
        this.uriInstanceMapping = new HashMap<String, Document>(instances.size());
        for (Document document : instances) {
            uriInstanceMapping.put(document.getDocumentURI(), document);
        }
    }

    protected Document getDocument(String uri) {
        if (uriInstanceMapping.containsKey(uri)) {
            return uriInstanceMapping.get(uri);
        } else {
            return null;
        }
    }

    protected <T extends Marking> List<T> getDocumentMarkings(String uri, Class<T> clazz) {
        Document result = this.getDocument(uri);
        if (result == null) {
            return new ArrayList<T>(0);
        } else {
            return result.getMarkings(clazz);
        }
    }

    @Override
    public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), TypedNamedEntity.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), TypedNamedEntity.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), TypedSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), Span.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), MeaningSpan.class);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), MeaningSpan.class);
    }
}
