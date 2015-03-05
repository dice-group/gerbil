package org.aksw.gerbil.matching.impl;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Span;

import com.carrotsearch.hppc.BitSet;

public class StrongSpanMatching<T extends Span> extends AbstractMatchingsCounter<T> {

    @Override
    protected int findMatching(T expectedElement, List<T> annotatorResult, BitSet alreadyUsedResults) {
        int eStart = expectedElement.getStartPosition();
        int eLength = expectedElement.getLength();
        for (int i = 0; i < annotatorResult.size(); ++i) {
            if ((!alreadyUsedResults.get(i)) && (eStart == annotatorResult.get(i).getStartPosition())
                    && (eLength == annotatorResult.get(i).getLength())) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

}
