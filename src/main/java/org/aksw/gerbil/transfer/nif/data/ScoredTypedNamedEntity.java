package org.aksw.gerbil.transfer.nif.data;

import java.util.Arrays;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.transfer.nif.TypedSpan;

public class ScoredTypedNamedEntity extends TypedNamedEntity implements ScoredMarking, TypedSpan, MeaningSpan {

    private double confidence;

    public ScoredTypedNamedEntity(int startPosition, int length, String uri, Set<String> types, double confidence) {
        super(startPosition, length, uri, types);
        this.confidence = confidence;
    }

    public ScoredTypedNamedEntity(int startPosition, int length, Set<String> uris, Set<String> types, double confidence) {
        super(startPosition, length, uris, types);
        this.confidence = confidence;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(confidence);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof ScoredTypedNamedEntity))
            return false;
        ScoredTypedNamedEntity other = (ScoredTypedNamedEntity) obj;
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
        builder.append(Arrays.toString(uris.toArray()));
        builder.append(", a ");
        builder.append(Arrays.toString(types.toArray()));
        builder.append(", ");
        builder.append(confidence);
        builder.append(')');
        return builder.toString();
    }
}
