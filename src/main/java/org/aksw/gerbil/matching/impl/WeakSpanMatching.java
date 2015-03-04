package org.aksw.gerbil.matching.impl;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Span;

public class WeakSpanMatching<T extends Span> extends AbstractMatchingsCounter<T> {

    @Override
    protected T findMatching(T expectedElement, Set<T> annotatorResult) {
        int eStart = expectedElement.getStartPosition();
        int eEnd = eStart + expectedElement.getLength();
        int rStart, rEnd;
        for (T result : annotatorResult) {
            rStart = result.getStartPosition();
            rEnd = rStart + result.getLength();
            if (rStart >= eStart) {
                if (rStart < eEnd) {
                    return result;
                }
            } else if (eStart < rEnd) {
                return result;
            }
        }
        return null;
    }

}
