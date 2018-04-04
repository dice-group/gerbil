package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.BitSet;

/**
 * This is a simple implementation of a MatchingsSearcher that uses the
 * {@link Object#equals(Object)} method of the given expected element to decide
 * which of the given annotator results is matching.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 */
public class EqualsBasedMatchingsSearcher<T extends Marking> implements MatchingsSearcher<T> {

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        BitSet matchings = new BitSet(annotatorResult.size());
        if (expectedElement == null) {
            for (int i = 0; i < annotatorResult.size(); ++i) {
                if (annotatorResult.get(i) == null) {
                    matchings.set(i);
                }
            }
        } else {
            for (int i = 0; i < annotatorResult.size(); ++i) {
                if ((!alreadyUsedResults.get(i)) && expectedElement.equals(annotatorResult.get(i))) {
                    matchings.set(i);
                }
            }
        }
        return matchings;
    }

}
