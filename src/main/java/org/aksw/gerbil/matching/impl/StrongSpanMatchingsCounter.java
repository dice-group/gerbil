package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class StrongSpanMatchingsCounter<T extends Span> implements MatchingsSearcher<T> {

    @Override
    public BitSet findMatchings(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eLength = expectedElement.getLength();
        BitSet matching = new BitSet(alreadyUsedResults.size());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if ((!alreadyUsedResults.get(i)) && (eStart == annotatorResult.get(i).getStartPosition())
                    && (eLength == annotatorResult.get(i).getLength())) {
                matching.set(i);
                // yes, we have found a matching position, but note, that we
                // have to find all matching positions!
            }
        }
        return matching;
    }

}
