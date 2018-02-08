package org.aksw.gerbil.transfer.nif.data;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.Relation;
import org.aksw.gerbil.transfer.nif.ScoredMarking;

public class ScoredRelationImpl extends RelationImpl implements ScoredMarking {

    protected double confidence;


    public ScoredRelationImpl(Relation relation, double confidence) throws CloneNotSupportedException {
        this((Meaning)relation.getSubject().clone(), 
        		(Meaning)relation.getPredicate().clone(), 
        		(Meaning)relation.getObject().clone(), confidence);
    }

    public ScoredRelationImpl(ScoredRelationImpl relation) throws CloneNotSupportedException {
        this(relation, relation.getConfidence());
    }
    
    public ScoredRelationImpl(Meaning subject, Meaning predicate, Meaning object, double confidence) {
    	super(subject, predicate, object);
    	setConfidence(confidence);
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
        builder.append("([");
        builder.append(this.getSubject()).append(", ");
        builder.append(this.getPredicate()).append(", ");
        builder.append(this.getObject());
        builder.append("], ");
        builder.append(confidence);
        builder.append(')');
        return builder.toString();
    }
}
