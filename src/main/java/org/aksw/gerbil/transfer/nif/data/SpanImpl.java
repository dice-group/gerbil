package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Span;

public class SpanImpl implements Span {

    protected int startPosition;
    protected int length;

    public SpanImpl(int startPosition, int length) {
        this.startPosition = startPosition;
        this.length = length;
    }

    @Override
    public int getStartPosition() {
        return startPosition;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + startPosition;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpanImpl other = (SpanImpl) obj;
        if (length != other.length)
            return false;
        if (startPosition != other.startPosition)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + startPosition + ", " + length + ")";
    }
}
