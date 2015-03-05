package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class WeakSpanMatching<T extends Span> extends AbstractMatchingsCounter<T> {

    @Override
    protected int findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eEnd = eStart + expectedElement.getLength();
        int rStart, rEnd;
        T result;
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if (!alreadyUsedResults.get(i)) {
                result = annotatorResult.get(i);
                rStart = result.getStartPosition();
                rEnd = rStart + result.getLength();
                if (rStart >= eStart) {
                    if (rStart < eEnd) {
                        return i;
                    }
                } else if (eStart < rEnd) {
                    return i;
                }
            }
        }
        return ELEMENT_NOT_FOUND;
    }

}
