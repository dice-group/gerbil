package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;

public class DocumentInformationReducer {

    public static Document reduceToPlainText(Document document) {
        return new DocumentImpl(document.getText(), document.getDocumentURI());
    }

    public static Document reduceToTextAndSpans(Document document) {
        List<Span> spans = document.getMarkings(Span.class);
        List<Marking> markings = new ArrayList<Marking>(spans.size());
        for (Span s : spans) {
            markings.add(new SpanImpl(s));
        }
        return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }
}
