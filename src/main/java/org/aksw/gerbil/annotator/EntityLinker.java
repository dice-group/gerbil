package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;

public interface EntityLinker extends Annotator {

    public List<MeaningSpan> performLinking(Document document) throws GerbilException;
}
