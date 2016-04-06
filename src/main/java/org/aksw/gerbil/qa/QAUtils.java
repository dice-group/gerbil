package org.aksw.gerbil.qa;

import java.util.Iterator;

import org.aksw.gerbil.qa.datatypes.AnswerItemType;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.qa.datatypes.Relation;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import com.hp.hpl.jena.vocabulary.RDF;

public class QAUtils {

    public static final String QUESTION_LANGUAGE = "en";

    protected static final Property RDF_TYPE_PROPERTY = new Property(RDF.type.getURI());

    public static Document translateQuestion(IQuestion question, String questionUri) {
        Document document = new DocumentImpl(question.getLanguageToQuestion().get(QUESTION_LANGUAGE), questionUri);
        // FIXME Ricardo, add the needed markings to the document
        String sparqlQueryString = question.getSparqlQuery();
        if (sparqlQueryString != null) {
            deriveMarkingsFromSparqlQuery(document, sparqlQueryString);
        }
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
        Query sparqlQuery = QueryFactory.create(sparqlQueryString);
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
                        // FIXME @Ricardo do we have to check for the ?uri in
                        // the subject?

                        if (object.isURI()) {
                            oAnnotation = new AnswerItemType(object.getURI());
                            document.addMarking(oAnnotation);
                        } else {
                            oAnnotation = null;
                        }

                        // the object is an Answer Item Type
                        document.addMarking(new AnswerItemType(object.getURI()));
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
                    if (subject.isVariable() || object.isVariable()) {
                        document.addMarking(new Relation(sAnnotation, pAnnotation, oAnnotation));
                    }
                }
            }
        };
        ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
    }

}
