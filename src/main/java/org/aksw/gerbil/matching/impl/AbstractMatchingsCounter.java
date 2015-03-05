package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

public abstract class AbstractMatchingsCounter<T extends Marking> implements MatchingsCounter<T> {

    protected static final int ELEMENT_NOT_FOUND = -1;

    protected List<int[]> counts = new ArrayList<int[]>();

    @Override
    public void countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        int documentCounts[] = new int[3];
        int matchingElementId;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        for (T expectedElement : goldStandard) {
            matchingElementId = findMatching(expectedElement, annotatorResult, alreadyUsedResults);
            if (matchingElementId != ELEMENT_NOT_FOUND) {
                ++documentCounts[TRUE_POSITIVE_COUNT_ID];
                alreadyUsedResults.set(matchingElementId);
            } else {
                ++documentCounts[FALSE_NEGATIVE_COUNT_ID];
            }
        }
        // The remaining elements are false positives
        documentCounts[FALSE_POSITIVE_COUNT_ID] = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        counts.add(documentCounts);
    }

    protected abstract int findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults);

    @Override
    public List<int[]> getCounts() {
        return counts;
    }

}
