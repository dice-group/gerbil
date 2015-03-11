package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Ignore;

@Ignore
public class TestEntityExtractor extends AbstractTestAnnotator implements EntityExtractor {

    public TestEntityExtractor(List<Document> instances) {
        super("TestEntityExtractor", false, instances, ExperimentType.EExt);
    }

    @Override
    public List<NamedEntity> performLinking(Document document) throws GerbilException {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<NamedEntity>(0);
        }
        return result.getMarkings(NamedEntity.class);
    }

    @Override
    public List<Span> performRecognition(Document document) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<Span>(0);
        }
        return result.getMarkings(Span.class);
    }

    @Override
    public List<NamedEntity> performExtraction(Document document) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<NamedEntity>(0);
        }
        return result.getMarkings(NamedEntity.class);
    }

}
