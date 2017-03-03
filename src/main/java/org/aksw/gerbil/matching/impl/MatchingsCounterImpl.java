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

import java.util.List;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.BitSet;

public class MatchingsCounterImpl<T extends Marking> implements MatchingsCounter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingsCounterImpl.class);

    private static boolean printDebugMsg = true;

    protected MatchingsSearcher<T> searcher;

    public MatchingsCounterImpl(MatchingsSearcher<T> searcher) {
        this.searcher = searcher;
    }

    @Override
    public EvaluationCounts countMatchings(List<T> annotatorResult, List<T> goldStandard) {
        EvaluationCounts documentCounts = new EvaluationCounts();
        BitSet matchingElements;
        BitSet alreadyUsedResults = new BitSet(annotatorResult.size());
        for (T expectedElement : goldStandard) {
            matchingElements = searcher.findMatchings(expectedElement, annotatorResult, alreadyUsedResults);
            if (!matchingElements.isEmpty()) {
                ++documentCounts.truePositives;
                alreadyUsedResults.set(matchingElements.nextSetBit(0));
                if (printDebugMsg && LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a true positive ({}).", expectedElement);
                }
            } else {
                ++documentCounts.falseNegatives;
                if (printDebugMsg && LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a false negative ({}).", expectedElement);
                }
            }
        }
        // The remaining elements are false positives
        documentCounts.falsePositives = (int) (annotatorResult.size() - alreadyUsedResults.cardinality());
        if (printDebugMsg && LOGGER.isDebugEnabled()) {
            for (int i = 0; i < annotatorResult.size(); ++i) {
                if (!alreadyUsedResults.get(i)) {
                    LOGGER.debug("Found a false positive ({}).", annotatorResult.get(i));
                }
            }
        }
        return documentCounts;
    }

    public static synchronized void setPrintDebugMsg(boolean flag) {
        printDebugMsg = flag;
    }

}
