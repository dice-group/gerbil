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
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ScoredEvaluationCountsArrayCreationTest {

    private static final double DELTA = 0.0000000001;

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testCases = new ArrayList<Object[]>();

        testCases.add(new Object[] { new ScoredEvaluationCounts[][] {}, new double[] {}, new int[] {}, new int[] {},
                new int[] {}, new int[][] {}, new int[][] {}, new int[][] {} });

        testCases
                .add(new Object[] { new ScoredEvaluationCounts[][] { new ScoredEvaluationCounts[] {} }, new double[] {},
                        new int[] {}, new int[] {}, new int[] {}, new int[][] {}, new int[][] {}, new int[][] {} });

        testCases.add(new Object[] {
                new ScoredEvaluationCounts[][] { new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                        new ScoredEvaluationCounts(0, 0, 1, 0.1) } },
                new double[] { 0, 0.1 }, new int[] { 1, 0 }, new int[] { 0, 0 }, new int[] { 0, 1 },
                new int[][] { { 1 }, { 0 } }, new int[][] { { 0 }, { 0 } }, new int[][] { { 0 }, { 1 } } });

        testCases.add(new Object[] {
                new ScoredEvaluationCounts[][] {
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.1) },
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.1) } },
                new double[] { 0, 0.1 }, new int[] { 2, 0 }, new int[] { 0, 0 }, new int[] { 0, 2 },
                new int[][] { { 1, 1 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 } },
                new int[][] { { 0, 0 }, { 1, 1 } } });

        testCases.add(new Object[] {
                new ScoredEvaluationCounts[][] {
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.1) },
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.3) } },
                new double[] { 0, 0.1, 0.3 }, new int[] { 2, 1, 0 }, new int[] { 0, 0, 0 }, new int[] { 0, 1, 2 },
                new int[][] { { 1, 1 }, { 0, 1 }, { 0, 0 } }, new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 } },
                new int[][] { { 0, 0 }, { 1, 0 }, { 1, 1 } } });

        testCases.add(new Object[] {
                new ScoredEvaluationCounts[][] { new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 2, 1, 0),
                        new ScoredEvaluationCounts(1, 1, 1, 0.1), new ScoredEvaluationCounts(1, 0, 1, 0.2),
                        new ScoredEvaluationCounts(0, 0, 2, 0.3) } },
                new double[] { 0, 0.1, 0.2, 0.3 }, new int[] { 1, 1, 1, 0 }, new int[] { 2, 1, 0, 0 },
                new int[] { 1, 1, 1, 2 }, new int[][] { { 1 }, { 1 }, { 1 }, { 0 } },
                new int[][] { { 2 }, { 1 }, { 0 }, { 0 } }, new int[][] { { 1 }, { 1 }, { 1 }, { 2 } } });

        testCases.add(new Object[] {
                new ScoredEvaluationCounts[][] {
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.1) },
                        new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 0, 0, 0),
                                new ScoredEvaluationCounts(0, 0, 1, 0.3) },
                new ScoredEvaluationCounts[] { new ScoredEvaluationCounts(1, 2, 1, 0),
                        new ScoredEvaluationCounts(1, 1, 1, 0.1), new ScoredEvaluationCounts(1, 0, 1, 0.2),
                        new ScoredEvaluationCounts(0, 0, 2, 0.3) } },
                new double[] { 0, 0.1, 0.2, 0.3 }, new int[] { 3, 2, 2, 0 }, new int[] { 2, 1, 0, 0 },
                new int[] { 1, 2, 2, 4 }, new int[][] { { 1, 1, 1 }, { 0, 1, 1 }, { 0, 1, 1 }, { 0, 0, 0 } },
                new int[][] { { 0, 0, 2 }, { 0, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } },
                new int[][] { { 0, 0, 1 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 2 } } });

        return testCases;
    }

    private ScoredEvaluationCounts counts[][];
    private double expectedScores[];
    private int expectedTPSums[];
    private int expectedFPSums[];
    private int expectedFNSums[];
    private int expectedTPs[][];
    private int expectedFPs[][];
    private int expectedFNs[][];

    public ScoredEvaluationCountsArrayCreationTest(ScoredEvaluationCounts[][] counts, double[] expectedScores,
            int[] expectedTPSums, int[] expectedFPSums, int[] expectedFNSums, int[][] expectedTPs, int[][] expectedFPs,
            int[][] expectedFNs) {
        this.counts = counts;
        this.expectedScores = expectedScores;
        this.expectedTPSums = expectedTPSums;
        this.expectedFPSums = expectedFPSums;
        this.expectedFNSums = expectedFNSums;
        this.expectedTPs = expectedTPs;
        this.expectedFPs = expectedFPs;
        this.expectedFNs = expectedFNs;
    }

    @Test
    public void test() {
        ScoredEvaluationCountsArray createdArray = ScoredEvaluationCountsArray.create(counts);
        Assert.assertArrayEquals(expectedScores, createdArray.scores, DELTA);
        Assert.assertArrayEquals(expectedTPSums, createdArray.truePositiveSums);
        Assert.assertArrayEquals(expectedFPSums, createdArray.falsePositiveSums);
        Assert.assertArrayEquals(expectedFNSums, createdArray.falseNegativeSums);
        Assert.assertEquals(expectedTPs.length, createdArray.truePositives.length);
        for (int i = 0; i < expectedTPs.length; ++i) {
            Assert.assertArrayEquals(expectedTPs[i], createdArray.truePositives[i]);
        }
        Assert.assertEquals(expectedFPs.length, createdArray.falsePositives.length);
        for (int i = 0; i < expectedFPs.length; ++i) {
            Assert.assertArrayEquals(expectedFPs[i], createdArray.falsePositives[i]);
        }
        Assert.assertEquals(expectedFNs.length, createdArray.falseNegatives.length);
        for (int i = 0; i < expectedFNs.length; ++i) {
            Assert.assertArrayEquals(expectedFNs[i], createdArray.falseNegatives[i]);
        }
    }
}
