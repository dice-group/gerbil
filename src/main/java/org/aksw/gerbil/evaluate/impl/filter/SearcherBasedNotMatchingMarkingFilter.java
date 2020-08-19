/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.evaluate.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorDecorator;
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

    /**
     * The MatchingsSearcher that is used to identify matching {@link Marking}s.
     */
    protected MatchingsSearcher<T> searcher;
    /**
     * This flag indicates whether a marking of the gold standard is allowed to
     * match several markings or only a single one.
     */
    protected boolean multiMatchingAllowed;

    /**
     * Constructor.
     * 
     * @param searcher
     *            The MatchingsSearcher that is used to identify matching
     *            {@link Marking}s.
     * @param evaluator
     *            The {@link Evaluator} that is decorated by this
     *            {@link EvaluatorDecorator}.
     * @param multiMatchingAllowed
     *            This flag indicates whether a marking of the gold standard is
     *            allowed to match several markings or only a single one.
     */
    public SearcherBasedNotMatchingMarkingFilter(MatchingsSearcher<T> searcher, Evaluator<T> evaluator,
            boolean multiMatchingAllowed) {
        super(evaluator);
        this.searcher = searcher;
        this.multiMatchingAllowed = multiMatchingAllowed;
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
                // if a multiple matching is not allowed, we have to mark the
                // Marking of the gold standard
                if (!multiMatchingAllowed) {
                    alreadyUsedResults.set(matchingElements.nextSetBit(0));
                }
            }
        }
        return filteredMarkings;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results,String language) {
        evaluator.evaluate(filterListOfMarkings(annotatorResults, goldStandard), goldStandard, results, language);
    }

}
