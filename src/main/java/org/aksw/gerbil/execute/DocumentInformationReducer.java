package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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

    public static Document reduceToTextAndEntities(Document document) {
        List<TypedNamedEntity> namedEntities = document.getMarkings(TypedNamedEntity.class);
        List<Marking> markings = new ArrayList<Marking>(namedEntities.size());
        for (TypedNamedEntity tne : namedEntities) {
            if (!(tne.getTypes().contains(RDFS.Class) || tne.getTypes().contains(OWL.Class))) {
                markings.add(new NamedEntity(tne.getStartPosition(), tne.getLength(), tne.getUri()));
            }
        }
        return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }
}
