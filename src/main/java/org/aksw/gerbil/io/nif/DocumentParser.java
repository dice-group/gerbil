package org.aksw.gerbil.io.nif;

import org.aksw.gerbil.io.nif.utils.NIFPositionHelper;
import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class DocumentParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentParser.class);

    private AnnotationParser annotationParser;
    private boolean removeUsedProperties;

    public DocumentParser() {
        this(false);
    }

    public DocumentParser(boolean removeUsedProperties) {
        this.removeUsedProperties = removeUsedProperties;
        annotationParser = new AnnotationParser(removeUsedProperties);
    }

    public Document getDocument(Model nifModel, Resource documentResource) {
        // Get the text of the document
        NodeIterator nodeIter = nifModel.listObjectsOfProperty(documentResource, NIF.isString);
        Literal tempLiteral = null;
        while (nodeIter.hasNext()) {
            if (tempLiteral != null) {
                LOGGER.warn("Got a document with more than one nif:isString properties. Using the last one.");
            }
            tempLiteral = nodeIter.next().asLiteral();
        }
        if (tempLiteral == null) {
            LOGGER.error("Got a document node without a text. Ignoring this document.");
            if (removeUsedProperties) {
                nifModel.removeAll(documentResource, null, null);
            }
            return null;
        }
        Document document = new DocumentImpl(tempLiteral.getString());
        document.setDocumentURI(NIFUriHelper.getDocumentUriFromNifUri(documentResource.getURI()));

        // Get the language of the text if it exists
        String lang = tempLiteral.getLanguage();
        if ((lang != null) && (lang.length() > 0)) {
            // FIXME add the language to the result document
        }

        annotationParser.parseAnnotations(nifModel, document, documentResource);
        if (removeUsedProperties) {
            nifModel.removeAll(documentResource, null, null);
        }

        NIFPositionHelper.correctAnnotationPositions(document);
        return document;
    }
}
