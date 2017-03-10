package org.aksw.gerbil.qa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.gerbil.qa.datatypes.AnswerItemType;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.AnswerType;
import org.aksw.gerbil.qa.datatypes.AnswerTypes;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.qa.datatypes.Relation;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJAnswers;
import org.aksw.qa.commons.load.json.QaldQuestionEntry;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.vocabulary.RDF;

public class QAUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAUtils.class);

    public static final String DEFAULT_QUESTION_LANGUAGE = "en";

    protected static final Property RDF_TYPE_PROPERTY = new Property(RDF.type.getURI());

    protected static final UrlValidator URL_VALIDATOR = new UrlValidator();

    @Deprecated
    public static Document translateQuestion(IQuestion question, String questionUri) {
    	return translateQuestion(question, questionUri, DEFAULT_QUESTION_LANGUAGE);
    }
    public static Document translateQuestion(IQuestion question, String questionUri, String questionLanguage) {
    	Document document = new DocumentImpl(question.getLanguageToQuestion().get(questionLanguage), questionUri);
        String sparqlQueryString = question.getSparqlQuery();
        // add the needed markings to the document
        // properties, answerItemType, relations, entities
        if (sparqlQueryString != null) {
            try {
                deriveMarkingsFromSparqlQuery(document, sparqlQueryString);
            } catch (Exception e) {
            }
        } else if (question.getPseudoSparqlQuery() != null) {
            try {
                deriveMarkingsFromSparqlQuery(document, question.getPseudoSparqlQuery());
            } catch (Exception e) {
            }
        }
        // FIXME @Ricardo if from annotator, load from IQuestion
        // answerType
        String answerTypeLabel = question.getAnswerType();
        if (answerTypeLabel != null) {
            answerTypeLabel = answerTypeLabel.toUpperCase();
            answerTypeLabel = answerTypeLabel.replace("RESOUCE", "RESOURCE");
            try {
                AnswerTypes answerType = AnswerTypes.valueOf(answerTypeLabel);
                document.addMarking(new AnswerType(answerType));
            } catch (Exception e) {
                LOGGER.error("Couldn't parse AnswerType " + answerTypeLabel + ". It will be ignored.", e);
            }
        }

        // add the answers
        Set<String> goldenAnswers = question.getGoldenAnswers();

        Marking answers = transformToAnnotations(goldenAnswers);
        if(answers == null){
            answers = new AnswerSet<String>(goldenAnswers);
        }
        document.addMarking(answers);
        return document;
    }

    /**
     * @deprecated set this to deprecated because it seems to contain a bug. The
     *             line "answers.add(ejA.toString());" does most probably not
     *             produce results as it is expected.
     */
    @Deprecated
    public static Document translateQuestion(QaldQuestionEntry question, String questionUri) {
        Document document = new DocumentImpl(question.getQuestion().get(0).getLanguage(), questionUri);
        String sparqlQueryString = question.getQuery().getSparql();
        // add the needed markings to the document
        // properties, answerItemType, relations, entities
        if (sparqlQueryString != null) {
            try {
                deriveMarkingsFromSparqlQuery(document, sparqlQueryString);
            } catch (Exception e) {
            }
        } else if (question.getQuery().getPseudo() != null) {
            try {
                deriveMarkingsFromSparqlQuery(document, question.getQuery().getPseudo());
            } catch (Exception e) {
            }
        }
        // FIXME @Ricardo if from annotator, load from IQuestion
        // answerType
        String answerTypeLabel = question.getAnswertype();
        if (answerTypeLabel != null) {
            answerTypeLabel = answerTypeLabel.toUpperCase();
            answerTypeLabel = answerTypeLabel.replace("RESOUCE", "RESOURCE");
            try {
                AnswerTypes answerType = AnswerTypes.valueOf(answerTypeLabel);
                document.addMarking(new AnswerType(answerType));
            } catch (Exception e) {
                LOGGER.error("Couldn't parse AnswerType " + answerTypeLabel + ". It will be ignored.", e);
            }
        }
        // add the answers
        Set<String> answers = new HashSet<String>();
        for (EJAnswers ejA : question.getAnswers()) {
            answers.add(ejA.toString());
        }
        document.addMarking(new AnswerSet<String>(answers));
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
    protected static void deriveMarkingsFromSparqlQuery(final Document document, String sparqlQueryString) {
        // handle text: prefixes of literals
        List<String> strings = null;
        if (sparqlQueryString.contains("text:\"")) {
            int wherePos = sparqlQueryString.toLowerCase().indexOf("where {");
            if (sparqlQueryString.contains("dbo:architecturalStyle")) {
                System.out.println("STOP!");
            }
            if (wherePos >= 0) {
                StringBuilder builder = new StringBuilder();
                String remainingString = sparqlQueryString.substring(wherePos);

                strings = new ArrayList<String>();
                Pattern pattern = Pattern.compile("text:\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(remainingString);
                int startPos, endPos = 7;
                while (matcher.find()) {
                    startPos = matcher.start();
                    builder.append(remainingString.substring(endPos, startPos));
                    builder.append("?text");
                    builder.append(strings.size());
                    strings.add(matcher.group(1));
                    endPos = matcher.end();
                }
                builder.append(remainingString.substring(endPos));
                remainingString = builder.toString();
                builder.delete(0, builder.length());

                builder.append(sparqlQueryString.substring(0, wherePos + 7));
                for (int i = 0; i < strings.size(); ++i) {
                    builder.append(" ?text");
                    builder.append(i);
                    builder.append(" <http://okbqa.org/text#query> \"");
                    builder.append(strings.get(i));
                    builder.append("\" .");
                }
                builder.append(remainingString);
                sparqlQueryString = builder.toString();
            }
        }

        Query sparqlQuery = null;
        try {
            sparqlQuery = QueryFactory.create(sparqlQueryString);
        } catch (Exception e) {
            LOGGER.error("Couldn't parse the given SPARQL Query \"" + sparqlQueryString
                    + "\". Throwing catched exception.", e);
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
                        document.addMarking(new Relation(sAnnotation, pAnnotation, object.getLiteralValue().toString()));
                    } else {
                        document.addMarking(new Relation(sAnnotation, pAnnotation, oAnnotation));
                    }
                }
            }
        };
        ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
    }

    /**
     * Transforms the given answers into a set of {@link Annotation} instances
     * if possible.
     * 
     * @param goldenAnswers
     *            answers that should be transformed into annotations
     * @return answer set as {@link Annotation} instances or null, if there was
     *         at least one answer that couldn't be transformed
     */
    public static AnswerSet<Annotation> transformToAnnotations(Set<String> answers) {
        for (String goldenAnswer : answers) {
            if (!URL_VALIDATOR.isValid(goldenAnswer)) {
                return null;
            }
        }
        Set<Annotation> annotations = new HashSet<>();
        for (String goldenAnswer : answers) {
            annotations.add(new Annotation(goldenAnswer));
        }
        return new AnswerSet<Annotation>(annotations);
    }
}
