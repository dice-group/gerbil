package org.aksw.gerbil.transfer.nif.data;

/**
 * A scored tag is a {@link Annotation} with a confidence score.
 * 
 * @author Michael Röder
 * 
 */
public class ScoredAnnotation extends Annotation {

    private double confidence;

    public ScoredAnnotation(String uri, double confidence) {
        super(uri);
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
        ScoredAnnotation other = (ScoredAnnotation) obj;
        if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ScoredAnnotation [confidence=" + confidence + ", uri=" + uri + "]";
    }

}
