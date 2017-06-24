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
package org.aksw.gerbil.matching.scored;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.utils.filter.ConfidenceScoreBasedMarkingFilter;

import com.carrotsearch.hppc.DoubleOpenHashSet;
import com.carrotsearch.hppc.ObjectArrayList;

/**
 * <p>
 * This implementation of the {@link ScoredMatchingsCounter} interface is a
 * decorator for a given {@link MatchingsCounter}. This {@link MatchingsCounter}
 * is called several times with different variants of filtered annotator result
 * lists which are generated based on the different confidence score thresholds.
 * </p>
 * <p>
 * Note that the returned array does not need to contain a single result for
 * every threshold that has been tested. Only those thresholds, that lead to a
 * different result are returned.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 */
public class ScoredMatchingsCounterImpl<T extends Marking> implements ScoredMatchingsCounter<T> {

    protected MatchingsCounter<T> counter;

    public ScoredMatchingsCounterImpl(MatchingsCounter<T> counter) {
        this.counter = counter;
    }

    @Override
    public ScoredEvaluationCounts[] countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        ObjectArrayList<ScoredEvaluationCounts> counts = new ObjectArrayList<ScoredEvaluationCounts>();
        ScoredEvaluationCounts lastCounts;
        // Everything has to be evaluated with a threshold = 0 at first!
        lastCounts = new ScoredEvaluationCounts(counter.countMatchings(annotatorResult, goldStandard), 0);
        counts.add(lastCounts);

        // create a list of confidence scores
        double scores[] = getConfidenceScores(annotatorResult);
        if (scores.length > 0) {
            Arrays.sort(scores);
            ConfidenceScoreBasedMarkingFilter<T> filter = new ConfidenceScoreBasedMarkingFilter<T>(0);
            List<T> filteredAnnotatorResult;
            EvaluationCounts currentCounts;
            for (int i = 0; i < scores.length; ++i) {
                filter.setThreshold(scores[i]);
                filteredAnnotatorResult = filter.filterList(annotatorResult);
                currentCounts = counter.countMatchings(filteredAnnotatorResult, goldStandard);
                if (!lastCounts.hasEqualCounts(currentCounts)) {
                    lastCounts = new ScoredEvaluationCounts(currentCounts, scores[i]);
                    counts.add(lastCounts);
                }
            }
        }
        return counts.toArray(ScoredEvaluationCounts.class);
    }

    protected double[] getConfidenceScores(List<T> annotatorResults) {
        DoubleOpenHashSet scores = new DoubleOpenHashSet();
        boolean foundMarkingWithoutScore = false;
        for (Marking result : annotatorResults) {
            if (result instanceof ScoredMarking) {
                scores.add(((ScoredMarking) result).getConfidence());
            } else {
                foundMarkingWithoutScore = true;
            }
        }
        // If there where no markings
        if ((scores.size() == 0) && (!foundMarkingWithoutScore)) {
            return new double[0];
        }
        // If there is a marking without a score
        if (foundMarkingWithoutScore) {
            // insert a score higher than all other representing all markings
            // without a score
            // double max = Double.NEGATIVE_INFINITY;
            // for (int i = 0; i < scores.allocated.length; i++) {
            // if (scores.allocated[i]) {
            // if (scores.keys[i] > max) {
            // max = scores.keys[i];
            // }
            // }
            // }
            // if (max < 1.0) {
            // scores.add(1.0);
            // } else {
            scores.add(Double.MAX_VALUE);
            // }
        }
        return scores.toArray();
    }
}
