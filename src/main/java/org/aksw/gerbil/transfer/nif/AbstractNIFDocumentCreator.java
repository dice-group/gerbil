/**
 * The MIT License (MIT)
 *
 * Copyright (C) ${year} Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.transfer.nif;

import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractNIFDocumentCreator implements NIFDocumentCreator {

    private String httpContentType;

    public AbstractNIFDocumentCreator(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    @Override
    public String getDocumentAsNIFString(Document document) {
        Model nifModel = createNIFModel(document);
        return generateNIFStringFromModel(nifModel);
    }

    protected abstract String generateNIFStringFromModel(Model nifModel);

    protected Model createNIFModel(Document document) {
        Model nifModel = ModelFactory.createDefaultModel();
        nifModel.setNsPrefixes(NIFTransferPrefixMapping.getInstance());
        // create the document node and add its properties
        String text = document.getText();
        int start = 0, end = text.codePointCount(0, text.length());
        StringBuilder documentUriBuilder = new StringBuilder();
        documentUriBuilder.append(document.getDocumentURI());
        documentUriBuilder.append("#char=");
        documentUriBuilder.append(start);
        documentUriBuilder.append(',');
        documentUriBuilder.append(end);

        Resource documentAsResource = nifModel.createResource(documentUriBuilder.toString());
        nifModel.add(documentAsResource, RDF.type, NIF.Context);
        nifModel.add(documentAsResource, RDF.type, NIF.String);
        nifModel.add(documentAsResource, RDF.type, NIF.RFC5147String);
        // TODO add language to String
        nifModel.add(documentAsResource, NIF.isString,
                nifModel.createTypedLiteral(document.getText(), XSDDatatype.XSDstring));
        nifModel.add(documentAsResource, NIF.beginIndex,
                nifModel.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(documentAsResource, NIF.endIndex,
                nifModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));

        // TODO add predominant language
        // http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#predLang

        // add annotations
        int markingId = 0;
        for (Marking marking : document.getMarkings()) {
            addMarking(nifModel, documentAsResource, text, document.getDocumentURI(), marking, markingId);
            ++markingId;
        }

        return nifModel;
    }

    private void addMarking(Model nifModel, Resource documentAsResource, String text, String documentURI,
            Marking marking, int markingId) {
        if (marking instanceof Span) {
            addSpan(nifModel, documentAsResource, text, documentURI, (Span) marking);
        } else if (marking instanceof Meaning) {
            addAnnotation(nifModel, documentAsResource, documentURI, (Annotation) marking, markingId);
        }
    }

    private void addAnnotation(Model nifModel, Resource documentAsResource, String documentURI, Annotation annotation,
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

    protected void addSpan(Model nifModel, Resource documentAsResource, String text, String documentURI, Span span) {
        int startInJavaText = span.getStartPosition();
        int endInJavaText = startInJavaText + span.getLength();
        int start = text.codePointCount(0, startInJavaText);
        int end = start + text.codePointCount(startInJavaText, endInJavaText);

        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#char=");
        uriBuilder.append(start);
        uriBuilder.append(',');
        uriBuilder.append(end);

        Resource spanAsResource = nifModel.createResource(uriBuilder.toString());
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

    @Override
    public String getHttpContentType() {
        return httpContentType;
    }
}
