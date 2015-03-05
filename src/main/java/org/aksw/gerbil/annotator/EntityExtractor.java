package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public interface EntityExtractor extends EntityLinker, EntityRecognizer {

    public List<NamedEntity> performExtraction(Document document);
}
