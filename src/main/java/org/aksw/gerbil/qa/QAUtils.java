package org.aksw.gerbil.qa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aksw.gerbil.qa.datatypes.AnswerItemType;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.AnswerType;
import org.aksw.gerbil.qa.datatypes.AnswerTypes;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.qa.datatypes.Relation;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import com.hp.hpl.jena.vocabulary.RDF;

public class QAUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAUtils.class);

    public static final String QUESTION_LANGUAGE = "en";

    protected static final Property RDF_TYPE_PROPERTY = new Property(RDF.type.getURI());

    public static Document translateQuestion(IQuestion question, String questionUri) {
        Document document = new DocumentImpl(question.getLanguageToQuestion().get(QUESTION_LANGUAGE), questionUri);
        String sparqlQueryString = question.getSparqlQuery();
        // add the needed markings to the document
        // properties, answerItemType, relations, entities
        if (sparqlQueryString != null) {
            deriveMarkingsFromSparqlQuery(document, sparqlQueryString);
        }
        // FIXME @Ricardo if from annotator, load from IQuestion
        // answerType
        String answerTypeLabel = question.getAnswerType();
        if (answerTypeLabel != null) {
            answerTypeLabel = answerTypeLabel.toUpperCase();
            AnswerTypes answerType = AnswerTypes.valueOf(answerTypeLabel);
            if (answerType != null) {
                document.addMarking(new AnswerType(answerType));
            } else {
                LOGGER.error("Couldn't parse AnswerType {}. It will be ignored.", answerTypeLabel);
            }
        }
        // add the answers
        document.addMarking(new AnswerSet(question.getGoldenAnswers()));
        return document;
    }

    /**
     * Adds {@link Annotation}, {@link Property}, {@link AnswerItemType} and
     * {@link Relation} markings to the document if they can be parsed from the
     * given questions SPARQL query.
     * 
     * @param document
     * @param question
     */
    protected static void deriveMarkingsFromSparqlQuery(final Document document, final String sparqlQueryString) {
        Query sparqlQuery = null;
        try {
            sparqlQuery = QueryFactory.create(sparqlQueryString);
        } catch (Exception e) {
            LOGGER.error(
                    "Couldn't parse the given SPARQL Query \"" + sparqlQueryString + "\". Throwing catched exception.",
                    e);
            throw e;
        }
        final Set<String> projectionVariables = new HashSet<String>(sparqlQuery.getResultVars());
        ElementVisitorBase ELB = new ElementVisitorBase() {
            public void visit(ElementPathBlock el) {
                Iterator<TriplePath> triples = el.patternElts();
                TriplePath triple;
                Node subject, predicate, object;
                Annotation sAnnotation, oAnnotation;
                Property pAnnotation;
                while (triples.hasNext()) {
                    triple = triples.next();
                    subject = triple.getSubject();
                    predicate = triple.getPredicate();
                    object = triple.getObject();
                    if (subject.isURI()) {
                        sAnnotation = new Annotation(subject.getURI());
                        document.addMarking(sAnnotation);
                    } else {
                        sAnnotation = null;
                    }
                    // If the predicate is rdf:type
                    if (predicate.equals(RDF.type)) {
                        oAnnotation = null;
                        // it is only an AnswerItemType if the subject is a
                        // variable and contained in the projection variables
                        if (subject.isVariable() && projectionVariables.contains(subject.getName()) && object.isURI()) {
                            oAnnotation = new AnswerItemType(object.getURI());
                            document.addMarking(oAnnotation);
                        }
                        // If the object is not an AnswerItemType but contains a
                        // URI
                        if ((oAnnotation == null) && object.isURI()) {
                            oAnnotation = new Annotation(object.getURI());
                        }
                        pAnnotation = RDF_TYPE_PROPERTY;
                    } else {
                        // we have found a property
                        pAnnotation = new Property(predicate.getURI());
                        document.addMarking(pAnnotation);
                        if (object.isURI()) {
                            oAnnotation = new Annotation(object.getURI());
                            document.addMarking(oAnnotation);
                        } else {
                            oAnnotation = null;
                        }
                    }
                    // Add the triple
                    if (object.isLiteral()) {
                        document.addMarking(
                                new Relation(sAnnotation, pAnnotation, object.getLiteralValue().toString()));
                    } else {
                        document.addMarking(new Relation(sAnnotation, pAnnotation, oAnnotation));
                    }
                }
            }
        };
        ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
    }

}
