/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif;

import java.util.Set;

import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
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
        for (String meainingUri : annotation.getUris()) {
            nifModel.add(annotationAsResource, ITSRDF.taIdentRef, nifModel.createResource(meainingUri));
        }

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

        if (span instanceof Meaning) {
            for (String meainingUri : ((Meaning) span).getUris()) {
                nifModel.add(spanAsResource, ITSRDF.taIdentRef, nifModel.createResource(meainingUri));
            }
        }
        if (span instanceof ScoredMarking) {
            nifModel.add(spanAsResource, ITSRDF.taConfidence,
                    nifModel.createTypedLiteral(((ScoredMarking) span).getConfidence(), XSDDatatype.XSDdouble));
        }
        if (span instanceof TypedMarking) {
            Set<String> types = ((TypedNamedEntity) span).getTypes();
            for (String type : types) {
                nifModel.add(spanAsResource, ITSRDF.taClassRef, nifModel.createResource(type));
            }
        }
    }
}
