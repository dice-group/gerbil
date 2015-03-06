package org.aksw.gerbil.io.nif;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class AnnotationParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationParser.class);

    private boolean removeUsedProperties;

    public AnnotationParser() {
        this(false);
    }

    public AnnotationParser(boolean removeUsedProperties) {
        this.removeUsedProperties = removeUsedProperties;
    }

    public void parseAnnotations(Model nifModel, Document document, Resource documentResource) {
        // get the annotations from the model
        List<Marking> markings = document.getMarkings();
        ResIterator resIter = nifModel.listSubjectsWithProperty(NIF.referenceContext, documentResource);
        Resource annotationResource;
        int start, end;
        String entityUri;
        double confidence;
        NodeIterator nodeIter;
        while (resIter.hasNext()) {
            annotationResource = resIter.next();
            start = end = -1;
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.beginIndex);
            if (nodeIter.hasNext()) {
                start = nodeIter.next().asLiteral().getInt();
            }
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, NIF.endIndex);
            if (nodeIter.hasNext()) {
                end = nodeIter.next().asLiteral().getInt();
            }
            if ((start >= 0) && (end >= 0)) {
                nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
                if (nodeIter.hasNext()) {
                    entityUri = nodeIter.next().toString();
                    nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
                    if (nodeIter.hasNext()) {
                        confidence = nodeIter.next().asLiteral().getDouble();
                        markings.add(new ScoredNamedEntity(start, end - start, entityUri, confidence));
                    } else {
                        // It has been disambiguated without a confidence
                        markings.add(new NamedEntity(start, end - start, entityUri));
                    }
                } else {
                    // It is a named entity that hasn't been disambiguated
                    markings.add(new SpanImpl(start, end - start));
                }
                // FIXME scored Span is missing
            } else {
                LOGGER.warn("Found an annotation resource (\"" + annotationResource.getURI()
                        + "\") without a start or end index. This annotation will be ignored.");
            }
            if (removeUsedProperties) {
                nifModel.removeAll(annotationResource, null, null);
            }
        }

        NodeIterator annotationIter = nifModel.listObjectsOfProperty(documentResource, NIF.topic);
        while (annotationIter.hasNext()) {
            annotationResource = annotationIter.next().asResource();
            nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taIdentRef);
            if (nodeIter.hasNext()) {
                entityUri = nodeIter.next().toString();
                nodeIter = nifModel.listObjectsOfProperty(annotationResource, ITSRDF.taConfidence);
                if (nodeIter.hasNext()) {
                    confidence = nodeIter.next().asLiteral().getDouble();
                    markings.add(new ScoredAnnotation(entityUri, confidence));
                } else {
                    markings.add(new Annotation(entityUri));
                }
            }
        }
    }
}
