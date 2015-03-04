package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;

public abstract class AbstractMatchingsCounter<T extends Marking> implements MatchingsCounter<T> {

    protected List<int[]> counts = new ArrayList<int[]>();

    @Override
    public void countMatchings(Set<T> annotatorResult, Set<T> goldStandard) {
        Set<T> resultCopy = new HashSet<T>(annotatorResult);
        int documentCounts[] = new int[4];
        T matchingElement;
        for (T expectedElement : goldStandard) {
            matchingElement = findMatching(expectedElement, resultCopy);
            if (matchingElement != null) {
                ++documentCounts[TRUE_POSITIVE_COUNT_ID];
                resultCopy.remove(matchingElement);
            } else {
                ++documentCounts[FALSE_NEGATIVE_COUNT_ID];
            }
        }
        // The remaining elements are false positives
        documentCounts[FALSE_POSITIVE_COUNT_ID] = resultCopy.size();
        counts.add(documentCounts);
    }

    protected abstract T findMatching(T expectedElement, Set<T> annotatorResult);

    @Override
    public List<int[]> getCounts() {
        return counts;
    }

}
