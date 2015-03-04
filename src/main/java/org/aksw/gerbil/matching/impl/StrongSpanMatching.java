package org.aksw.gerbil.matching.impl;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Span;

public class StrongSpanMatching<T extends Span> extends AbstractMatchingsCounter<T> {

    @Override
    protected T findMatching(T expectedElement, Set<T> annotatorResult) {
        int eStart = expectedElement.getStartPosition();
        int eLength = expectedElement.getLength();
        for (T result : annotatorResult) {
            if ((eStart == result.getStartPosition()) && (eLength == result.getLength())) {
                return result;
            }
        }
        return null;
    }

}
