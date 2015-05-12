package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.junit.Ignore;

@Ignore
public class TestEntityExtractor extends AbstractTestAnnotator implements EntityExtractor {

    public TestEntityExtractor(List<Document> instances) {
        super("TestEntityExtractor", false, instances, ExperimentType.OKE_Task1);
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<MeaningSpan>(0);
        }
        return result.getMarkings(MeaningSpan.class);
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
    public List<MeaningSpan> performExtraction(Document document) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<MeaningSpan>(0);
        }
        return result.getMarkings(MeaningSpan.class);
    }

}
