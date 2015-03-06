package org.aksw.gerbil.io.nif;

import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class DocumentWriter {

    private AnnotationWriter annotationWriter = new AnnotationWriter();

    public void writeDocumentToModel(Model nifModel, Document document) {
        // create the document node and add its properties
        String text = document.getText();
        int end = text.codePointCount(0, text.length());
        String documentUri = NIFUriHelper.getNifUri(document, end);
        Resource documentResource = nifModel.createResource(documentUri);
        nifModel.add(documentResource, RDF.type, NIF.Context);
        nifModel.add(documentResource, RDF.type, NIF.String);
        nifModel.add(documentResource, RDF.type, NIF.RFC5147String);
        // TODO add language to String
        nifModel.add(documentResource, NIF.isString,
                nifModel.createTypedLiteral(document.getText(), XSDDatatype.XSDstring));
        nifModel.add(documentResource, NIF.beginIndex,
                nifModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
        nifModel.add(documentResource, NIF.endIndex,
                nifModel.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        // TODO add predominant language
        // http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#predLang

        // add annotations
        int meaningId = 0;
        for (Marking marking : document.getMarkings()) {
            if (marking instanceof Span) {
                annotationWriter.addSpan(nifModel, documentResource, text, document.getDocumentURI(), (Span) marking);
            } else if (marking instanceof Meaning) {
                annotationWriter.addAnnotation(nifModel, documentResource, document.getDocumentURI(),
                        (Annotation) marking, meaningId);
                ++meaningId;
            }
        }
    }

}
