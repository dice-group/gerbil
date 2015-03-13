package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.BitSet;

public class MatchingsCounterImpl<T extends Marking> implements MatchingsCounter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingsCounterImpl.class);

    protected List<int[]> counts = new ArrayList<int[]>();
    protected MatchingsSearcher<T> searcher;

    public MatchingsCounterImpl(MatchingsSearcher<T> searcher) {
        this.searcher = searcher;
    }

    @Override
    public void countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        int documentCounts[] = new int[3];
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        for (T expectedElement : goldStandard) {
            matchingElements = searcher.findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                ++documentCounts[TRUE_POSITIVE_COUNT_ID];
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
                LOGGER.debug("Found a true positive (" + expectedElement + ").");
            } else {
                ++documentCounts[FALSE_NEGATIVE_COUNT_ID];
                LOGGER.debug("Found a false negative (" + expectedElement + ").");
            }
        }
        // The remaining elements are false positives
        documentCounts[FALSE_POSITIVE_COUNT_ID] = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        LOGGER.debug("Found " + documentCounts[FALSE_POSITIVE_COUNT_ID] + " false positives.");
        counts.add(documentCounts);
    }

    @Override
    public List<int[]> getCounts() {
        return counts;
    }

}
