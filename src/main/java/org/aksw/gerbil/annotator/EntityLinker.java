package org.aksw.gerbil.annotator;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public interface EntityLinker extends Annotator {

    public Set<NamedEntity> performLinking(Document document);
}
