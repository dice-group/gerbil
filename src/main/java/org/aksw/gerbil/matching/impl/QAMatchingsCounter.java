package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.agdistis.util.Triple;
import org.aksw.agdistis.util.TripleIndex;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.ResourceAnswerSet;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

@SuppressWarnings("rawtypes")
public class QAMatchingsCounter implements MatchingsCounter<AnswerSet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAMatchingsCounter.class);

    private TripleIndex index;
    private UrlValidator urlValidator;
    private MatchingsCounterImpl meaningMatchingsCounter;
    private SameAsRetriever retriever;


    
    public QAMatchingsCounter(TripleIndex index, UrlValidator urlValidator, UriKBClassifier classifier) {
        this(index, urlValidator, classifier, null);
    }

    public QAMatchingsCounter(TripleIndex index, UrlValidator urlValidator, UriKBClassifier classifier,
            SameAsRetriever retriever) {
        this.index = index;
        this.urlValidator = urlValidator;
        this.meaningMatchingsCounter = new MatchingsCounterImpl<Meaning>(
                new ClassifierBasedMeaningMatchingsSearcher<Meaning>(classifier));
        this.retriever = retriever;

    }

    @SuppressWarnings("unchecked")
    @Override
    public EvaluationCounts countMatchings(List<AnswerSet> annotatorResult, List<AnswerSet> goldStandard) {
        AnswerSet<?> annotatorAnswerSet, goldStdAnswerSet;
        // Set<String> annotatorAnswers;
        if (annotatorResult.size() == 0) {
            annotatorAnswerSet = new AnswerSet<String>(new HashSet<String>(0));
        } else {
            if (annotatorResult.size() > 1) {
                LOGGER.warn("Got more than one AnswerSet element from the annotator. Only the first will be used while all others are ignored.");
            }
            annotatorAnswerSet = annotatorResult.get(0);
            // annotatorAnswers = (Set<String>)
            // annotatorResult.get(0).getAnswers();
        }
        // FIXME the question type is never needed
        // QuestionType questionType = null;
        // Set<String> goldStandardAnswers;
        if (goldStandard.size() == 0) {
            goldStdAnswerSet = new AnswerSet<String>(new HashSet<String>(0));
        } else {
            if (goldStandard.size() > 1) {
                LOGGER.warn("Got more than one AnswerSet element from the gold standard. Only the first will be used while all others are ignored.");
            }
            goldStdAnswerSet = goldStandard.get(0);
            // goldStandardAnswers = as.getAnswers();
            // if (as instanceof ClassifiedAnswerSet) {
            // questionType = ((ClassifiedAnswerSet) as).getQuestionType();
            // }
        }
        // if (questionType == null) {
        // LOGGER.info("Couldn't determine the question type from the gold
        // standard. Assuming the SELECT type.");
        // questionType = QuestionType.SELECT;
        // }
        Set<String> goldStrings = null, resultStrings = null;
        List<Annotation> goldAnnotations = null, resultAnnotations = null;
        if (goldStdAnswerSet instanceof ResourceAnswerSet) {
            goldAnnotations = new ArrayList<Annotation>(((ResourceAnswerSet) goldStdAnswerSet).getAnswers());
            if (annotatorAnswerSet instanceof ResourceAnswerSet) {
                resultAnnotations = new ArrayList<Annotation>(((ResourceAnswerSet) annotatorAnswerSet).getAnswers());
            } else {
                // Force the creation of Annotations
                resultAnnotations = forceAnnotationCreation((Set<String>) annotatorAnswerSet.getAnswers());
            }
        } else {
        	//Check if goldStd answer is Annotation and results is Annotation
        	if(goldStdAnswerSet.getAnswers().iterator().next() instanceof Annotation){
                goldAnnotations = new ArrayList<Annotation>(((AnswerSet) goldStdAnswerSet).getAnswers());
                if (annotatorAnswerSet.getAnswers().iterator().next() instanceof Annotation) {
                    resultAnnotations = new ArrayList<Annotation>(((ResourceAnswerSet) annotatorAnswerSet).getAnswers());
                } else {
                    // Force the creation of Annotations
                    resultAnnotations = forceAnnotationCreation((Set<String>) annotatorAnswerSet.getAnswers());
                }
        	}
            goldStrings = (Set<String>) goldStdAnswerSet.getAnswers();
            resultStrings = (Set<String>) annotatorAnswerSet.getAnswers();
        }

        if (goldAnnotations != null) {
            return meaningMatchingsCounter.countMatchings(resultAnnotations, goldAnnotations);
        } else {
            return countMatchings(resultStrings, goldStrings);
        }
    }

    private List<Annotation> forceAnnotationCreation(Set<String> answers) {
        List<Annotation> annotations = new ArrayList<Annotation>(answers.size());
        for (String answer : answers) {
            // If the String is a valid URL
            if (urlValidator.isValid(answer)) {
                annotations.add(new Annotation(answer));
            } else {
                // The given String might be a label. Try to find all matching
                // resources
                List<Triple> triples = null;
                if (index != null) {
                    triples = index.search(null, "http://www.w3.org/2000/01/rdf-schema#label", answer, 10000);
                }
                // If matching triples have been found
                if ((triples != null) && (triples.size() > 0)) {
                    for (Triple triple : triples) {
                        if (triple.getObject().equals(answer)) {
                            annotations.add(new Annotation(triple.getSubject()));
                        }
                    }
                } else {
                    // There are no matching resources. Use the String as URI
                    annotations.add(new Annotation(answer));
                }
            }
        }
        // If there is a same as retriever, us it
        if (retriever != null) {
            for (Annotation annotation : annotations) {
                retriever.addSameURIs(annotation.getUris());
            }
        }
        return annotations;
    }

    protected <T> EvaluationCounts countMatchings(Set<T> annotatorAnswers, Set<T> goldStandardAnswers) {
        int truePositives = Sets.intersection(annotatorAnswers, goldStandardAnswers).size();
        return new EvaluationCounts(truePositives, annotatorAnswers.size() - truePositives, goldStandardAnswers.size()
                - truePositives);
    }

}
