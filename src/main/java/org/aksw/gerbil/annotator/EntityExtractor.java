package org.aksw.gerbil.annotator;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public interface EntityExtractor extends EntityLinker, EntityRecognizer {

    public Set<NamedEntity> performExtraction(Document document);
}
