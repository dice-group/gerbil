package org.aksw.gerbil.annotator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Ignore;

@Ignore
public class TestEntityLinker extends AbstractTestAnnotator implements EntityLinker {

    public TestEntityLinker(List<Document> instances) {
        super("TestEntityLinker", false, instances, ExperimentType.ELink);
    }

    @Override
    public List<NamedEntity> performLinking(Document document) throws GerbilException {
        Document result = this.getDocument(document.getDocumentURI());
        if (result == null) {
            return new ArrayList<NamedEntity>(0);
        }
        return result.getMarkings(NamedEntity.class);
    }

}
