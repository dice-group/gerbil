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
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

public class QAMatchingsCounterTest {

    @SuppressWarnings("rawtypes")
    private static final MatchingTestExample EXAMPLES[] = new MatchingTestExample[] {
            // empty test case
            new MatchingTestExample<>(new AnswerSet[] {}, new AnswerSet[] {}, new int[] { 0, 0, 0 }),
            // test case with empty annotator results
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1")) },
                    new AnswerSet[] {}, new int[] { 0, 1, 0 }),
            // test case with empty gold standard
            new MatchingTestExample<AnswerSet>(new AnswerSet[] {},
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1")) }, new int[] { 0, 0, 1 }),
            // test case with single exact matching AnswerSets
            new MatchingTestExample<AnswerSet>(new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1")) },
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1")) }, new int[] { 1, 0, 0 }),
            // test case with several exact matching AnswerSets
            new MatchingTestExample<AnswerSet>(
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1", "http://kb/2", "http://kb/3")) },
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1", "http://kb/2", "http://kb/3")) },
                    new int[] { 3, 0, 0 }),
            // test case with several exact matching AnswerSets with a different
            // order
            new MatchingTestExample<AnswerSet>(
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1", "http://kb/2", "http://kb/3")) },
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/2", "http://kb/3", "http://kb/1")) },
                    new int[] { 3, 0, 0 }),
            // test case with one exact matching AnswerSets, one wrong matching
            // and a missing matching
            new MatchingTestExample<AnswerSet>(
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1", "http://ukb/2")) },
                    new AnswerSet[] { new AnswerSet(Sets.newHashSet("http://kb/1", "http://kb/2", "http://kb/3")) },
                    new int[] { 1, 1, 2 }) };

    private MatchingsCounter<AnswerSet> counter;
    private List<List<AnswerSet>> annotatorResult;
    private List<List<AnswerSet>> goldStandard;
    private List<EvaluationCounts> expectedCounts;

    @SuppressWarnings("unchecked")
    public QAMatchingsCounterTest() {
        this.counter = new QAMatchingsCounter();
        this.annotatorResult = new ArrayList<List<AnswerSet>>(EXAMPLES.length);
        this.goldStandard = new ArrayList<List<AnswerSet>>(EXAMPLES.length);
        this.expectedCounts = new ArrayList<EvaluationCounts>(EXAMPLES.length);
        for (int i = 0; i < EXAMPLES.length; i++) {
            annotatorResult.add(EXAMPLES[i].annotatorResult);
            goldStandard.add(EXAMPLES[i].goldStandard);
            expectedCounts.add(EXAMPLES[i].expectedCounts);
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
