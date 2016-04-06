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
package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractMatchingsCounterTest<T extends Marking> {

    private MatchingsCounter<T> counter;
    private List<List<T>> annotatorResult;
    private List<List<T>> goldStandard;
    private List<EvaluationCounts> expectedCounts;

    public AbstractMatchingsCounterTest(MatchingsSearcher<T> searcher, MatchingTestExample<T> testExamples[]) {
        this.counter = new MatchingsCounterImpl<T>(searcher);
        this.annotatorResult = new ArrayList<List<T>>(testExamples.length);
        this.goldStandard = new ArrayList<List<T>>(testExamples.length);
        this.expectedCounts = new ArrayList<EvaluationCounts>(testExamples.length);
        for (int i = 0; i < testExamples.length; i++) {
            annotatorResult.add(testExamples[i].annotatorResult);
            goldStandard.add(testExamples[i].goldStandard);
            expectedCounts.add(testExamples[i].expectedCounts);
        }
    }

    @Test
    public void test() {
        EvaluationCounts counts[] = new EvaluationCounts[annotatorResult.size()];
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = counter.countMatchings(annotatorResult.get(i), goldStandard.get(i));
        }
        for (int i = 0; i < counts.length; ++i) {
            Assert.assertEquals("Counts of the element " + i + " are different.", expectedCounts.get(i), counts[i]);
        }
    }
}
