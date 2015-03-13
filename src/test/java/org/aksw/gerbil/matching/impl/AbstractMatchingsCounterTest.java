package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractMatchingsCounterTest<T extends Marking> {

    private MatchingsCounter<T> counter;
    private List<List<T>> annotatorResult;
    private List<List<T>> goldStandard;
    private List<int[]> expectedCounts;

    public AbstractMatchingsCounterTest(MatchingsSearcher<T> searcher, MatchingTestExample<T> testExamples[]) {
        this.counter = new MatchingsCounterImpl<T>(searcher);
        this.annotatorResult = new ArrayList<List<T>>(testExamples.length);
        this.goldStandard = new ArrayList<List<T>>(testExamples.length);
        this.expectedCounts = new ArrayList<int[]>(testExamples.length);
        for (int i = 0; i < testExamples.length; i++) {
            annotatorResult.add(testExamples[i].annotatorResult);
            goldStandard.add(testExamples[i].goldStandard);
            expectedCounts.add(testExamples[i].expectedCounts);
        }
    }

    @Test
    public void test() {
        for (int i = 0; i < annotatorResult.size(); ++i) {
            counter.countMatchings(annotatorResult.get(i), goldStandard.get(i));
        }
        List<int[]> counts = counter.getCounts();
        for (int i = 0; i < counts.size(); ++i) {
            Assert.assertArrayEquals("Counts of the element " + i + " are different.", expectedCounts.get(i),
                    counts.get(i));
        }
    }
}
