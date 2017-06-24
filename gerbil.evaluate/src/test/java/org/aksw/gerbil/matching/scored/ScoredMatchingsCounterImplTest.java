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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.WeakSpanMatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.ScoredSpanImpl;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.carrotsearch.hppc.DoubleObjectOpenHashMap;

@RunWith(Parameterized.class)
public class ScoredMatchingsCounterImplTest {

    public static final int TRUE_POSITIVE_COUNT_ID = 0;
    public static final int FALSE_POSITIVE_COUNT_ID = 1;
    public static final int FALSE_NEGATIVE_COUNT_ID = 2;

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testCases = new ArrayList<Object[]>();
        // empty test case
        testCases.add(new Object[] { new Span[] {}, new Span[] {}, new double[] { 0 }, new int[][] { { 0, 0, 0 } } });
        // test case with empty gold standard
        testCases.add(new Object[] { new Span[] { new SpanImpl(0, 10) }, new Span[] {}, new double[] { 0 },
                new int[][] { { 0, 1, 0 } } });
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(0, 10, 0.1) }, new Span[] {},
                new double[] { 0, 0.1 }, new int[][] { { 0, 1, 0 }, { 0, 0, 0 } } });
        // test case with empty annotator results
        testCases.add(new Object[] { new Span[] {}, new Span[] { new SpanImpl(0, 10) }, new double[] { 0 },
                new int[][] { { 0, 0, 1 } } });
        // test case with single exact matching Spans
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(0, 10, 0.1) }, new Span[] { new SpanImpl(1, 10) },
                new double[] { 0, 0.1 }, new int[][] { { 1, 0, 0 }, { 0, 0, 1 } } });
        // test case with several exact matching Spans
        testCases.add(new Object[] { new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0 },
                new int[][] { { 2, 0, 0 } } });
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(0, 10, 0.1), new ScoredSpanImpl(20, 10, 0.2) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0, 0.1, 0.2 },
                new int[][] { { 2, 0, 0 }, { 1, 0, 1 }, { 0, 0, 2 } } });
        // test case with one matching pair and another not matching pair
        testCases.add(new Object[] { new Span[] { new SpanImpl(60, 10), new SpanImpl(20, 10) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0 },
                new int[][] { { 1, 1, 1 } } });
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(60, 10, 0.1), new ScoredSpanImpl(20, 10, 0.1) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0, 0.1 },
                new int[][] { { 1, 1, 1 }, { 0, 0, 2 } } });
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(60, 10, 0.1), new ScoredSpanImpl(20, 10, 0.2) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0, 0.1, 0.2 },
                new int[][] { { 1, 1, 1 }, { 1, 0, 1 }, { 0, 0, 2 } } });
        testCases.add(new Object[] { new Span[] { new ScoredSpanImpl(60, 10, 0.2), new ScoredSpanImpl(20, 10, 0.1) },
                new Span[] { new SpanImpl(0, 10), new SpanImpl(20, 10) }, new double[] { 0, 0.1, 0.2 },
                new int[][] { { 1, 1, 1 }, { 0, 1, 2 }, { 0, 0, 2 } } });
        return testCases;
    }

    private Span annotatorResult[];
    private Span goldStandard[];
    private DoubleObjectOpenHashMap<int[]> expectedResults;

    public ScoredMatchingsCounterImplTest(Span annotatorResult[], Span goldStandard[],
            double[] expectedConfidenceScores, int[][] expectedCounts) {
        this.annotatorResult = annotatorResult;
        this.goldStandard = goldStandard;
        this.expectedResults = new DoubleObjectOpenHashMap<int[]>();
        for (int i = 0; i < expectedConfidenceScores.length; ++i) {
            this.expectedResults.put(expectedConfidenceScores[i], expectedCounts[i]);
        }
    }

    @Test
    public void test() {
        ScoredMatchingsCounterImpl<Span> counterImpl = new ScoredMatchingsCounterImpl<Span>(
                new MatchingsCounterImpl<Span>(new WeakSpanMatchingsSearcher<Span>()));
        ScoredEvaluationCounts counts[] = counterImpl.countMatchings(Arrays.asList(annotatorResult),
                Arrays.asList(goldStandard));
        int expectedCounts[];
        for (int i = 0; i < counts.length; ++i) {
            Assert.assertTrue("Didn't expected the threshold " + counts[i].confidenceThreshould + ".",
                    expectedResults.containsKey(counts[i].confidenceThreshould));
            expectedCounts = expectedResults.get(counts[i].confidenceThreshould);
            Assert.assertEquals(expectedCounts[TRUE_POSITIVE_COUNT_ID], counts[i].truePositives);
            Assert.assertEquals(expectedCounts[FALSE_POSITIVE_COUNT_ID], counts[i].falsePositives);
            Assert.assertEquals(expectedCounts[FALSE_NEGATIVE_COUNT_ID], counts[i].falseNegatives);
        }
        Assert.assertEquals(expectedResults.size(), counts.length);
    }
}
