package org.aksw.gerbil.evaluate.impl;

import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedSpanMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorDecorator;
import org.aksw.gerbil.matching.MatchingsSearcher;

import com.carrotsearch.hppc.BitSet;

/**
 * This {@link EvaluatorDecorator} classifies the given
 * {@link ClassifiedSpanMeaning} regarding the {@link MarkingClasses#GS_IN_KB}
 * class. Note that its implementation is based on the assumption that the
 * {@link ClassifiedSpanMeaning}s
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 */
public class GSInKBClassifyingEvaluatorDecorator<T extends ClassifiedSpanMeaning>
        extends AbstractEvaluatorDecorator<T> {

    protected MatchingsSearcher<T> searcher;

    public GSInKBClassifyingEvaluatorDecorator(Evaluator<T> evaluator, MatchingsSearcher<T> searcher) {
        super(evaluator);
        this.searcher = searcher;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        classify(annotatorResults, goldStandard);
        evaluator.evaluate(annotatorResults, goldStandard, results);
    }

    protected void classify(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        List<T> goldList;
        for (int i = 0; i < annotatorResults.size(); ++i) {
            goldList = goldStandard.get(i);
            classifyGoldStdList(goldList);
            classifyAnnotatorList(annotatorResults.get(i), goldList);
        }
    }

    private void classifyGoldStdList(List<T> goldStandard) {
        for (T marking : goldStandard) {
            if (marking.hasClass(MarkingClasses.IN_KB)) {
                marking.setClass(MarkingClasses.GS_IN_KB);
            }
        }
    }

    private void classifyAnnotatorList(List<T> annotatorResults, List<T> goldStandard) {
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(goldStandard.size());
        int matchingElementId;
        boolean elementInKb;
        for (T marking : annotatorResults) {
            matchingElements = searcher.findMatchings(marking, goldStandard, alreadyUsedResults);
            // search for matching elements that have the InKB class
            matchingElementId = matchingElements.nextSetBit(0);
            if (matchingElementId >= 0) {
                elementInKb = false;
                while ((matchingElementId >= 0) && (!elementInKb)) {
                    elementInKb = goldStandard.get(matchingElementId).hasClass(MarkingClasses.IN_KB);
                    matchingElementId = matchingElements.nextSetBit(matchingElementId + 1);
                }
                if (elementInKb) {
                    marking.setClass(MarkingClasses.GS_IN_KB);
                }
            }
        }
    }

}
