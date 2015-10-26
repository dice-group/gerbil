package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.ScoredSpan;
import org.aksw.gerbil.transfer.nif.Span;

public class ScoredSpanImpl extends SpanImpl implements ScoredSpan {

    private double confidence;

    public ScoredSpanImpl(int startPosition, int length, double confidence) {
        super(startPosition, length);
        this.confidence = confidence;
    }

    public ScoredSpanImpl(Span span, double confidence) {
        super(span);
        this.confidence = confidence;
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    @Override
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp = Double.doubleToLongBits(confidence);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof ScoredSpanImpl))
            return false;
        ScoredSpanImpl other = (ScoredSpanImpl) obj;
        if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(startPosition);
        builder.append(", ");
        builder.append(length);
        builder.append(", ");
        builder.append(confidence);
        builder.append(')');
        return builder.toString();
    }

}
