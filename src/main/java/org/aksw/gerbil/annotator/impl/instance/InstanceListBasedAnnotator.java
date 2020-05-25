package org.aksw.gerbil.annotator.impl.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.C2KBAnnotator;
import org.aksw.gerbil.annotator.D2KBAnnotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.EntityTyper;
import org.aksw.gerbil.annotator.OKE2018Task4Annotator;
import org.aksw.gerbil.annotator.OKETask1Annotator;
import org.aksw.gerbil.annotator.OKETask2Annotator;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

public class InstanceListBasedAnnotator extends AbstractAnnotator implements A2KBAnnotator, C2KBAnnotator,
        D2KBAnnotator, EntityRecognizer, EntityTyper, OKETask1Annotator, OKETask2Annotator, OKE2018Task4Annotator {

    /*
     * The mapping has been changed to contain the length since we encountered
     * problems with some datasets containing a document URI more than once.
     * Inside the NIF file this is not a problem because the length is added to
     * the document URI. However, since we remove the positions from the URIs,
     * we have to add the length in this class.
     */
    /**
     * Mapping of URI + text.length() to the documents.
     */
    protected Map<String, Document> uriInstanceMapping;

    public InstanceListBasedAnnotator(String annotatorName, List<Document> instances) {
        super(annotatorName);
        this.uriInstanceMapping = new HashMap<String, Document>(instances.size());
        for (Document document : instances) {
            uriInstanceMapping.put(generateDocUri(document.getDocumentURI(), document.getText().length()), document);
        }
    }

    protected Document getDocument(String uri, int textLength) {
        String mappingUri = generateDocUri(uri, textLength);
        if (uriInstanceMapping.containsKey(mappingUri)) {
            return uriInstanceMapping.get(mappingUri);
        } else {
            return null;
        }
    }

    protected static String generateDocUri(String uri, int textLength) {
        StringBuilder builder = new StringBuilder(uri.length() + 10);
        builder.append(uri);
        builder.append('_');
        builder.append(textLength);
        return builder.toString();
    }

    protected <T extends Marking> List<T> getDocumentMarkings(String uri, int textLength, Class<T> clazz) {
        Document result = this.getDocument(uri, textLength);
        if (result == null) {
            return new ArrayList<T>(0);
        } else {
            return result.getMarkings(clazz);
        }
    }

    @Override
    public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), TypedNamedEntity.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), TypedNamedEntity.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), TypedSpan.class);
    }

    @Override
    public List<Span> performRecognition(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), Span.class);
    }

    @Override
    public List<MeaningSpan> performD2KBTask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), MeaningSpan.class);
    }

    @Override
    public List<Meaning> performC2KB(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), Meaning.class);
    }

    @Override
    public List<MeaningSpan> performA2KBTask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), MeaningSpan.class);
    }

    @Override
    public List<TypedSpan> performRT2KBTask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), TypedSpan.class);
    }

	@Override
	public List<Relation> performRETask(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), Relation.class);

	}

	@Override
	public List<Marking> performOKE2018Task4(Document document) throws GerbilException {
        return getDocumentMarkings(document.getDocumentURI(), document.getText().length(), Marking.class);
	}
	
	public Collection<Document> getInstances() {
	    return uriInstanceMapping.values();
	}
}
