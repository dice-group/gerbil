package org.aksw.gerbil.matching.impl;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.aksw.agdistis.util.Triple;
import org.aksw.agdistis.util.TripleIndex;
import org.aksw.gerbil.dataset.converter.Literal2ResourceManager;
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
import com.google.common.collect.Sets.SetView;

@SuppressWarnings("rawtypes")
public class QAMatchingsCounter implements MatchingsCounter<AnswerSet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QAMatchingsCounter.class);

	private TripleIndex index;
	private UrlValidator urlValidator;
	private MatchingsCounterImpl meaningMatchingsCounter;
	private SameAsRetriever retriever;
	private Literal2ResourceManager converterManager;

	public QAMatchingsCounter(TripleIndex index, UrlValidator urlValidator, UriKBClassifier classifier,
			Literal2ResourceManager converterManager) {
		this(index, urlValidator, classifier, null, converterManager);
	}

	public QAMatchingsCounter(TripleIndex index, UrlValidator urlValidator, UriKBClassifier classifier,
			SameAsRetriever retriever, Literal2ResourceManager converterManager) {
		this.index = index;
		this.urlValidator = urlValidator;
		this.meaningMatchingsCounter = new MatchingsCounterImpl<Meaning>(
				new ClassifierBasedMeaningMatchingsSearcher<Meaning>(classifier));
		this.retriever = retriever;
		this.converterManager = converterManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvaluationCounts countMatchings(List<AnswerSet> annotatorResult, List<AnswerSet> goldStandard) {
		AnswerSet<?> annotatorAnswerSet, goldStdAnswerSet;

		// Check if results has only one result otherwise print message
		if (annotatorResult.size() == 0) {
			annotatorAnswerSet = new AnswerSet<String>(new HashSet<String>(0));
		} else {
			if (annotatorResult.size() > 1) {
				LOGGER.warn(
						"Got more than one AnswerSet element from the annotator. Only the first will be used while all others are ignored.");
			}
			annotatorAnswerSet = annotatorResult.get(0);
		}

		// Check if gold std has only one result otherwise print message
		if (goldStandard.size() == 0) {
			goldStdAnswerSet = new AnswerSet<String>(new HashSet<String>(0));
		} else {
			if (goldStandard.size() > 1) {
				LOGGER.warn(
						"Got more than one AnswerSet element from the gold standard. Only the first will be used while all others are ignored.");
			}
			goldStdAnswerSet = goldStandard.get(0);
		}

		// get all Annotations from gs
		List<Annotation> goldStdAnnotations = new LinkedList<Annotation>(),
				annotatorAnnotations = new LinkedList<Annotation>();
		Set<String> goldStdStrings = new HashSet<String>(), annotatorStrings = new HashSet<String>();

		Set<?> resultsSet = annotatorAnswerSet.getAnswers();
		
		for (Object answerGS : goldStdAnswerSet.getAnswers()) {
			// if gs answer is annotation
			if (answerGS instanceof Annotation) {
				// add to annotations
				goldStdAnnotations.add((Annotation) answerGS);
				
			} else {
				// otherwise to strings
				String answer = "";
				if(answerGS!=null){
					answer = answerGS.toString();
				}else{
					//if there is no value in an answer, it is invalid format, however we will assume that the answer is just empty as default.
					//FIXME maybe we should throw a detailed error here, so the user knows what's wrong.
					answer="";
				}
				goldStdStrings.add(answer);
			}
		}
		Iterator<?> it = resultsSet.iterator();
		while(it.hasNext() ) {
			Object answerAR = it.next();
			// if gs answer is annotation
			if (answerAR instanceof Annotation) {
				// add to annotations
				annotatorAnnotations.add((Annotation) answerAR);
				
			} else {
				// otherwise to strings
				annotatorStrings.add(answerAR.toString());
			}
		}

		EvaluationCounts global = new EvaluationCounts();
		/*
		 * The order here is critical as afirst the strings are compared.
		 *
		 * as we can assume that if a GS is a string the annotator eitehr provides a string or
		 * a false annotation. 
		 * The remaining annotator results can then be forced to annotations and be added 
		 * to the actual annotation results of the system
		 */
		EvaluationCounts stringCounts = countMatchings(annotatorStrings, goldStdStrings);
		stringCounts.falsePositives=0;
		SetView<String> diff = Sets.difference(annotatorStrings, goldStdStrings);
		
		Set<String> annotatorDiff = new HashSet<String>(annotatorStrings);
		annotatorDiff.removeAll(goldStdStrings);
		Set<String> goldDiff = new HashSet<String>(goldStdStrings);
		goldDiff.removeAll(annotatorStrings);
		annotatorAnnotations.addAll(forceAnnotationCreation(annotatorDiff));
		goldStdAnnotations.addAll(forceAnnotationCreation(goldDiff));

		EvaluationCounts annotationCounts = meaningMatchingsCounter.countMatchings(annotatorAnnotations,
				goldStdAnnotations);
		//all which are found can be added
		global.setTruePositives(stringCounts.truePositives+annotationCounts.truePositives);
		/*
		 * all which are not found by the system will be viewed in the anno comp,
		 * as the string comp will guide the fn and fp through and the annotation comp will 
		 * then check if they are really fp / fn or if they were an annotation in the other set
		 */
		global.setFalseNegatives(annotationCounts.falseNegatives);
		global.setFalsePositives(annotationCounts.falsePositives);
		return global;

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
				
				for (String received : converterManager.getResourcesForLiteral(answer)) {
					annotations.add(new Annotation(received));
				}
				if (answer.matches(".*@\\w\\w(-\\w\\w\\w\\w)?$")) {
					answer = answer.substring(0, answer.lastIndexOf("@"));
					
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
		return new EvaluationCounts(truePositives, annotatorAnswers.size() - truePositives,
				goldStandardAnswers.size() - truePositives);
	}

}
