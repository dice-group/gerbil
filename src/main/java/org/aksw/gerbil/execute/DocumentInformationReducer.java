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
import org.aksw.gerbil.utils.filter.MarkingFilter;
import org.aksw.gerbil.utils.filter.TypeBasedMarkingFilter;

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
        MarkingFilter<TypedNamedEntity> filter = new TypeBasedMarkingFilter<TypedNamedEntity>(false,
                RDFS.Class.getURI(), OWL.Class.getURI());
        List<TypedNamedEntity> namedEntities = document.getMarkings(TypedNamedEntity.class);
        List<Marking> markings = new ArrayList<Marking>(namedEntities.size());
        for (TypedNamedEntity tne : namedEntities) {
            if (filter.isMarkingGood(tne)) {
                markings.add(new NamedEntity(tne.getStartPosition(), tne.getLength(), tne.getUris()));
            }
        }
        return new DocumentImpl(document.getText(), document.getDocumentURI(), markings);
    }
}
