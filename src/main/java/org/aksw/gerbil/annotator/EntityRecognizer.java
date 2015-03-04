package org.aksw.gerbil.annotator;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public interface EntityRecognizer extends Annotator {

    public Set<NamedEntity> performRecognition(Document document);
}
