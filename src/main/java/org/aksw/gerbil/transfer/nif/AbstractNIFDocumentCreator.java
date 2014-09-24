package org.aksw.gerbil.transfer.nif;

import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredDisambigAnnotation;
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
    public String getDocumentAsNIFString(AnnotatedDocument document) {
        Model nifModel = createNIFModel(document);
        return generateNIFStringFromModel(nifModel);
    }

    protected abstract String generateNIFStringFromModel(Model nifModel);

    protected Model createNIFModel(AnnotatedDocument document) {
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
        for (Annotation annotation : document.getAnnotations()) {
            addAnnotation(nifModel, documentAsResource, text, document.getDocumentURI(), annotation);
        }

        return nifModel;
    }

    protected void addAnnotation(Model nifModel, Resource documentAsResource, String text, String documentURI,
            Annotation annotation) {
        int startInJavaText = annotation.getStartPosition();
        int endInJavaText = startInJavaText + annotation.getLength();
        int start = text.codePointCount(0, startInJavaText);
        int end = start + text.codePointCount(startInJavaText, endInJavaText);

        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#char=");
        uriBuilder.append(start);
        uriBuilder.append(',');
        uriBuilder.append(end);

        Resource annotationAsResource = nifModel.createResource(uriBuilder.toString());
        nifModel.add(annotationAsResource, RDF.type, NIF.String);
        nifModel.add(annotationAsResource, RDF.type, NIF.RFC5147String);
        // TODO add language to String
        nifModel.add(annotationAsResource, NIF.anchorOf,
                nifModel.createTypedLiteral(text.substring(startInJavaText, endInJavaText), XSDDatatype.XSDstring));
        nifModel.add(annotationAsResource, NIF.beginIndex,
                nifModel.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(annotationAsResource, NIF.endIndex,
                nifModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(annotationAsResource, NIF.referenceContext, documentAsResource);

        if (annotation instanceof DisambiguatedAnnotation) {
            nifModel.add(annotationAsResource, ITSRDF.taIdentRef, nifModel
                    .createResource(((DisambiguatedAnnotation) annotation).getUri()));
        }
        if (annotation instanceof ScoredDisambigAnnotation) {
            nifModel.add(annotationAsResource, ITSRDF.taConfidence, nifModel.createTypedLiteral(
                    ((ScoredDisambigAnnotation) annotation).getConfidence(), XSDDatatype.XSDdouble));
        }
    }

    @Override
    public String getHttpContentType() {
        return httpContentType;
    }
}
