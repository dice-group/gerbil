package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.junit.Ignore;

@Ignore
public class TestEntityRecognizer extends AbstractTestAnnotator implements EntityRecognizer {

    public TestEntityRecognizer(List<Document> instances) {
        super("TestEntityRecognizer", false, instances, ExperimentType.ERec);
    }

    @Override
    public List<Span> performRecognition(Document document) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<Span>(0);
        }
        return result.getMarkings(Span.class);
    }

}
