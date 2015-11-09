package org.aksw.gerbil.evaluate.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

/**
 * This evaluator decorator removes every marking from the given list that does
 * not match the given gold standard list based on a given
 * {@link MatchingsSearcher} instance.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class SearcherBasedNotMatchingMarkingFilter<T extends Marking> extends AbstractEvaluatorDecorator<T> {

    protected MatchingsSearcher<T> searcher;

    public SearcherBasedNotMatchingMarkingFilter(MatchingsSearcher<T> searcher, Evaluator<T> evaluator) {
        super(evaluator);
        this.searcher = searcher;
    }

    protected List<List<T>> filterListOfMarkings(List<List<T>> markings, List<List<T>> goldStandard) {
        List<List<T>> filteredMarkings = new ArrayList<List<T>>(markings.size());
        for (int i = 0; i < markings.size(); ++i) {
            filteredMarkings.add(filterMarkings(markings.get(i), goldStandard.get(i)));
        }
        return filteredMarkings;
    }

    protected List<T> filterMarkings(List<T> markings, List<T> goldStandard) {
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(goldStandard.size());
        List<T> filteredMarkings = new ArrayList<T>(markings.size());
        for (T marking : markings) {
            matchingElements = searcher.findMatchings(marking, goldStandard, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                filteredMarkings.add(marking);
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
            }
        }
        return filteredMarkings;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        evaluator.evaluate(filterListOfMarkings(annotatorResults, goldStandard), goldStandard, results);
    }

}
