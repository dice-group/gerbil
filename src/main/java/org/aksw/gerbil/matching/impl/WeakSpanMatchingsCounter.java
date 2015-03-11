package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class WeakSpanMatchingsCounter<T extends Span> extends AbstractMatchingsCounter<T> {

    @Override
    protected BitSet findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eEnd = eStart + expectedElement.getLength();
        int rStart, rEnd;
        T result;
        BitSet matching = new BitSet(alreadyUsedResults.size());
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                result = annotatorResult.get(i);
                rStart = result.getStartPosition();
                rEnd = rStart + result.getLength();
                if (rStart >= eStart) {
                    if (rStart < eEnd) {
                        matching.set(i);
                        // yes, we have found a matching position, but note,
                        // that we have to find all matching positions!
                    }
                } else if (eStart < rEnd) {
                    matching.set(i);
                }
            }
        }
        return matching;
    }

}
