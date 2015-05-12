package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;

public interface EntityExtractor extends EntityLinker, EntityRecognizer {

    public List<MeaningSpan> performExtraction(Document document) throws GerbilException;
}
