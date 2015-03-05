package org.aksw.gerbil.annotator;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;

public interface EntityRecognizer extends Annotator {

    public List<Span> performRecognition(Document document);
}
