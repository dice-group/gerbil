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
