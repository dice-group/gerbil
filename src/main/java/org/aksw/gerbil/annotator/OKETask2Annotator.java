package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

public interface OKETask2Annotator extends Annotator {

    public List<TypedNamedEntity> performTask2(Document document) throws GerbilException;

}
