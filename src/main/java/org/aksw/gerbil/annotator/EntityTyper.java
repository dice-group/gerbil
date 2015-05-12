package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.TypedSpan;

public interface EntityTyper extends Annotator {

    public List<TypedSpan> performTyping(Document document) throws GerbilException;
}
