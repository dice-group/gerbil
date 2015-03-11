package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public abstract class AbstractMatchingsCounter<T extends Marking> implements MatchingsCounter<T> {

    protected List<int[]> counts = new ArrayList<int[]>();

    @Override
    public void countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        int documentCounts[] = new int[3];
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        for (T expectedElement : goldStandard) {
            matchingElements = findMatching(expectedElement, annotatorResult, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                ++documentCounts[TRUE_POSITIVE_COUNT_ID];
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
            } else {
                ++documentCounts[FALSE_NEGATIVE_COUNT_ID];
            }
        }
        // The remaining elements are false positives
        documentCounts[FALSE_POSITIVE_COUNT_ID] = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        counts.add(documentCounts);
    }

    protected abstract BitSet findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults);

    @Override
    public List<int[]> getCounts() {
        return counts;
    }

}
