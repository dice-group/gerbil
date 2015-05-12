package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Ignore;

@Ignore
public class TestOKETask1Annotator extends AbstractTestAnnotator implements OKETask1Annotator {

    public TestOKETask1Annotator(List<Document> instances) {
        super("TestOKETask1Annotator", false, instances, ExperimentType.OKE_Task1);
    }

    public <T extends Marking> List<T> performAnnotation(Document document, Class<T> markingClass) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<T>(0);
        }
        return result.getMarkings(markingClass);
    }

    @Override
    public List<Span> performRecognition(Document document) {
        return performAnnotation(document, Span.class);
    }

    @Override
    public List<MeaningSpan> performExtraction(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<MeaningSpan> performLinking(Document document) throws GerbilException {
        return performAnnotation(document, MeaningSpan.class);
    }

    @Override
    public List<TypedSpan> performTyping(Document document) throws GerbilException {
        return performAnnotation(document, TypedSpan.class);
    }

    @Override
    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException {
        return performAnnotation(document, TypedNamedEntity.class);
    }

}
