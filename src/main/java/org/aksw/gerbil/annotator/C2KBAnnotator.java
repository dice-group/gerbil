package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;

public interface C2KBAnnotator extends Annotator {

    public List<Meaning> performC2KB(Document document) throws GerbilException;
}
