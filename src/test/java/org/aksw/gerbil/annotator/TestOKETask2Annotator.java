package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Ignore;

@Ignore
public class TestOKETask2Annotator extends AbstractTestAnnotator implements OKETask2Annotator {

    public TestOKETask2Annotator(List<Document> instances) {
        super("TestOKETask1Annotator", false, instances, ExperimentType.OKE_Task2);
    }

    public <T extends Marking> List<T> performAnnotation(Document document, Class<T> markingClass) {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<T>(0);
        }
        return result.getMarkings(markingClass);
    }

    @Override
    public List<TypedNamedEntity> performTask2(Document document) throws GerbilException {
        return performAnnotation(document, TypedNamedEntity.class);
    }

}
