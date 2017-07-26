package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.apache.jena.graph.Triple;

public class ScoredRelationImpl extends RelationImpl implements ScoredMarking {

    protected double confidence;

    public ScoredRelationImpl(Triple relation, double confidence) {
        super(relation);
        setConfidence(confidence);
    }

    public ScoredRelationImpl(Relation relation, double confidence) {
        this(relation.getRelation(), confidence);
    }

    public ScoredRelationImpl(ScoredRelationImpl relation) {
        this(relation.getRelation(), relation.getConfidence());
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
    public Object clone() throws CloneNotSupportedException {
        return new ScoredRelationImpl(this);
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
        if (getClass() != obj.getClass())
            return false;
        ScoredRelationImpl other = (ScoredRelationImpl) obj;
        if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(relation.toString());
        builder.append(", ");
        builder.append(confidence);
        builder.append(')');
        return builder.toString();
    }
}
