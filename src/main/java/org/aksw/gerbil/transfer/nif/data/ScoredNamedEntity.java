package org.aksw.gerbil.transfer.nif.data;

public class ScoredNamedEntity extends NamedEntity {

    private double confidence;

    public ScoredNamedEntity(int startPosition, int length, String uri, double confidence) {
        super(startPosition, length, uri);
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
        if (getClass() != obj.getClass())
            return false;
        ScoredNamedEntity other = (ScoredNamedEntity) obj;
        if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + startPosition + ", " + length + ", " + uri + ", " + confidence + ")";
    }

}
