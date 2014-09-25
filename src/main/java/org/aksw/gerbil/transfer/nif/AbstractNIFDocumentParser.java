package org.aksw.gerbil.transfer.nif;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.transfer.nif.data.AnnotatedDocumentImpl;
import org.aksw.gerbil.transfer.nif.data.AnnotationImpl;
import org.aksw.gerbil.transfer.nif.data.DisambiguatedAnnotation;
import org.aksw.gerbil.transfer.nif.data.EndPosBasedComparator;
import org.aksw.gerbil.transfer.nif.data.ScoredDisambigAnnotation;
import org.aksw.gerbil.transfer.nif.data.StartPosBasedComparator;
import org.aksw.gerbil.transfer.nif.vocabulary.ITSRDF;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractNIFDocumentParser implements NIFDocumentParser {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractNIFDocumentParser.class);

    private String httpContentType;

    public AbstractNIFDocumentParser(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    @Override
    public AnnotatedDocument getDocumentFromNIFString(String nifString)
            throws Exception {
        return getDocumentFromNIFReader(new StringReader(nifString));
    }

    @Override
    public AnnotatedDocument getDocumentFromNIFReader(Reader reader)
            throws Exception {
        Model nifModel = parseNIFModelFromReader(reader);
        return createAnnotatedDocument(nifModel);
    }

    protected abstract Model parseNIFModelFromReader(Reader reader) throws Exception;

    protected AnnotatedDocument createAnnotatedDocument(Model nifModel)
            throws Exception {
        // Try to get the resource describing the document
        ResIterator resIter = nifModel.listResourcesWithProperty(RDF.type,
                NIF.Context);
        List<Resource> resources = new ArrayList<Resource>();
        while (resIter.hasNext()) {
            resources.add(resIter.next());
        }
        if (resources.size() == 0) {
            LOGGER.error("Couldn't find the document resource inside the parsed NIF model.");
            throw new Exception(
                    "Couldn't find the document resource inside the parsed NIF model.");
        }
        if (resources.size() > 1) {
            LOGGER.warn("Got a NIF model with more than one resource of the type nif:Context. Only the first one will be used.");
        }

        Resource document = resources.get(0);

        // Get the text of the document
        NodeIterator nodeIter = nifModel.listObjectsOfProperty(document,
                NIF.isString);
        Literal tempLiteral = null;
        while (nodeIter.hasNext()) {
            if (tempLiteral != null) {
                LOGGER.warn("Got a document with more than one nif:isString properties. Using the last one.");
            }
            tempLiteral = nodeIter.next().asLiteral();
        }
        if (tempLiteral == null) {
            LOGGER.error("Got a document node without a text.");
            throw new Exception("Got a document node without a text.");
        }
        AnnotatedDocument resultDocument = new AnnotatedDocumentImpl(
                tempLiteral.getString());
        String documentURI = document.getURI();
        int pos = documentURI.lastIndexOf('#');
        if (pos > 0) {
            resultDocument.setDocumentURI(documentURI.substring(0, pos));
        }

        // Get the language of the text if it exists
        String lang = tempLiteral.getLanguage();
        if ((lang != null) && (lang.length() > 0)) {
            // FIXME add the language to the result document
        }

        // get the annotations from the model
        List<Annotation> annotations = resultDocument.getAnnotations();
        resIter = nifModel.listSubjectsWithProperty(NIF.referenceContext,
                document);
        Resource annotationResource;
        int start, end;
        String entityUri;
        double confidence;
        while (resIter.hasNext()) {
            annotationResource = resIter.next();
            start = end = -1;
            nodeIter = nifModel.listObjectsOfProperty(annotationResource,
                    NIF.beginIndex);
            if (nodeIter.hasNext()) {
                start = nodeIter.next().asLiteral().getInt();
            }
            nodeIter = nifModel.listObjectsOfProperty(annotationResource,
                    NIF.endIndex);
            if (nodeIter.hasNext()) {
                end = nodeIter.next().asLiteral().getInt();
            }
            if ((start >= 0) && (end >= 0)) {
                nodeIter = nifModel.listObjectsOfProperty(annotationResource,
                        ITSRDF.taIdentRef);
                if (nodeIter.hasNext()) {
                    entityUri = nodeIter.next().toString();
                    nodeIter = nifModel.listObjectsOfProperty(
                            annotationResource, ITSRDF.taConfidence);
                    if (nodeIter.hasNext()) {
                        confidence = nodeIter.next().asLiteral().getDouble();
                        annotations.add(new ScoredDisambigAnnotation(start, end
                                - start, entityUri, confidence));
                    } else {
                        // It has been disambiguated without a confidence
                        annotations.add(new DisambiguatedAnnotation(start, end
                                - start, entityUri));
                    }
                } else {
                    // It is a named entity that hasn't been disambiguated
                    annotations.add(new AnnotationImpl(start, end - start));
                }
            } else {
                LOGGER.warn("Found an annotation resource (\""
                        + annotationResource.getURI()
                        + "\") without a start or end index. This annotation will be ignored.");
            }
        }

        correctAnnotationPositions(resultDocument);
        return resultDocument;
    }

    /**
     * The positions in NIF are measured in codepoints, while Java counts in terms of characters. So we have to correct
     * the positions of the annotations.
     * 
     * @param resultDocument
     */
    protected void correctAnnotationPositions(AnnotatedDocument resultDocument) {
        List<Annotation> annotations = resultDocument.getAnnotations();
        Collections.sort(annotations, new StartPosBasedComparator());
        List<Annotation> annotationsSortedByEnd = new ArrayList<Annotation>(annotations);
        Collections.sort(annotationsSortedByEnd, new EndPosBasedComparator());
        int startPositions[] = new int[annotations.size()];
        int endPositions[] = new int[annotations.size()];
        Annotation currentAnnotation;
        for (int i = 0; i < annotations.size(); ++i) {
            startPositions[i] = annotations.get(i).getStartPosition();
            currentAnnotation = annotationsSortedByEnd.get(i);
            endPositions[i] = currentAnnotation.getStartPosition() + currentAnnotation.getLength();
        }
        String text = resultDocument.getText();
        int codePointsCount = 0;
        int posInStart = 0, posInEnd = 0;
        for (int i = 0; i < text.length(); ++i) {
            codePointsCount += text.codePointCount(i, i + 1);
            while ((posInStart < startPositions.length) && (codePointsCount > startPositions[posInStart])) {
                annotations.get(posInStart).setStartPosition(i);
                ++posInStart;
            }
            while ((posInEnd < endPositions.length) && (codePointsCount > endPositions[posInEnd])) {
                currentAnnotation = annotationsSortedByEnd.get(posInEnd);
                currentAnnotation.setLength(i - currentAnnotation.getStartPosition());
                ++posInEnd;
            }
        }
    }

    @Override
    public String getHttpContentType() {
        return httpContentType;
    }

}
