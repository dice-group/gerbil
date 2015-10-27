/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
