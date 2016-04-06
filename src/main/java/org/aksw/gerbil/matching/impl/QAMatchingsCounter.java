package org.aksw.gerbil.matching.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.qa.datatypes.ClassifiedAnswerSet;
import org.aksw.gerbil.qa.datatypes.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class QAMatchingsCounter implements MatchingsCounter<AnswerSet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAMatchingsCounter.class);

    @Override
    public EvaluationCounts countMatchings(List<AnswerSet> annotatorResult, List<AnswerSet> goldStandard) {
        Set<String> annotatorAnswers;
        if (annotatorResult.size() == 0) {
            annotatorAnswers = new HashSet<String>();
        } else {
            if (annotatorResult.size() > 1) {
                LOGGER.warn(
                        "Got more than one AnswerSet element from the annotator. Only the first will be used while all others are ignored.");
            }
            annotatorAnswers = annotatorResult.get(0).getAnswers();
        }
        QuestionType questionType = null;
        Set<String> goldStandardAnswers;
        if (goldStandard.size() == 0) {
            goldStandardAnswers = new HashSet<String>();
        } else {
            if (goldStandard.size() > 1) {
                LOGGER.warn(
                        "Got more than one AnswerSet element from the gold standard. Only the first will be used while all others are ignored.");
            }
            AnswerSet as = goldStandard.get(0);
            goldStandardAnswers = as.getAnswers();
            if (as instanceof ClassifiedAnswerSet) {
                questionType = ((ClassifiedAnswerSet) as).getQuestionType();
            }
        }
        if (questionType == null) {
            LOGGER.warn("Couldn't determine the question type from the gold standard. Assuming the SELECT type.");
            questionType = QuestionType.SELECT;
        }
        // FIXME @Ricardo it would be possible to distinguish based on the query
        // type
        // switch (questionType) {
        // case SELECT:
        // return countMatchingsOfSelect(annotatorAnswers, goldStandardAnswers);
        // case ASK:
        // return countMatchingsOfAsk(annotatorAnswers, goldStandardAnswers);
        // }
        return countMatchings(annotatorAnswers, goldStandardAnswers);
    }

    protected EvaluationCounts countMatchings(Set<String> annotatorAnswers, Set<String> goldStandardAnswers) {
        int truePositives = Sets.intersection(annotatorAnswers, goldStandardAnswers).size();
        return new EvaluationCounts(truePositives, annotatorAnswers.size() - truePositives,
                goldStandardAnswers.size() - truePositives);
    }

}
