package org.aksw.gerbil.io.nif;

import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class AnnotationWriter {

    public void writeMarkingToModel(Model nifModel, Resource documentResource, String text, String documentURI,
            Marking marking, int markingId) {
        if (marking instanceof Span) {
            addSpan(nifModel, documentResource, text, documentURI, (Span) marking);
        } else if (marking instanceof Meaning) {
            addAnnotation(nifModel, documentResource, documentURI, (Annotation) marking, markingId);
        }
    }

    public void addAnnotation(Model nifModel, Resource documentAsResource, String documentURI, Annotation annotation,
            int annotationId) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#annotation");
        uriBuilder.append(annotationId);

        Resource annotationAsResource = nifModel.createResource(uriBuilder.toString());
        nifModel.add(annotationAsResource, RDF.type, NIF.Annotation);
        nifModel.add(documentAsResource, NIF.topic, annotationAsResource);
        nifModel.add(annotationAsResource, ITSRDF.taIdentRef, nifModel.createResource(annotation.getUri()));

        if (annotation instanceof ScoredAnnotation) {
            nifModel.add(annotationAsResource, NIF.confidence,
                    Double.toString(((ScoredAnnotation) annotation).getConfidence()), XSDDatatype.XSDstring);
        }
    }

    public void addSpan(Model nifModel, Resource documentAsResource, String text, String documentURI, Span span) {
        int startInJavaText = span.getStartPosition();
        int endInJavaText = startInJavaText + span.getLength();
        int start = text.codePointCount(0, startInJavaText);
        int end = start + text.codePointCount(startInJavaText, endInJavaText);

        String spanUri = NIFUriHelper.getNifUri(documentURI, start, end);
        Resource spanAsResource = nifModel.createResource(spanUri);
        nifModel.add(spanAsResource, RDF.type, NIF.String);
        nifModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
        // TODO add language to String
        nifModel.add(spanAsResource, NIF.anchorOf,
                nifModel.createTypedLiteral(text.substring(startInJavaText, endInJavaText), XSDDatatype.XSDstring));
        nifModel.add(spanAsResource, NIF.beginIndex,
                nifModel.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(spanAsResource, NIF.endIndex, nifModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(spanAsResource, NIF.referenceContext, documentAsResource);

        if (span instanceof NamedEntity) {
            nifModel.add(spanAsResource, ITSRDF.taIdentRef, nifModel.createResource(((NamedEntity) span).getUri()));
        }
        if (span instanceof ScoredNamedEntity) {
            nifModel.add(spanAsResource, ITSRDF.taConfidence,
                    nifModel.createTypedLiteral(((ScoredNamedEntity) span).getConfidence(), XSDDatatype.XSDdouble));
        }
    }
}
