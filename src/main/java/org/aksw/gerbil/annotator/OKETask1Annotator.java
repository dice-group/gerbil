package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

public interface OKETask1Annotator extends EntityExtractor, EntityTyper {

    public List<TypedNamedEntity> performTask1(Document document) throws GerbilException;
}
