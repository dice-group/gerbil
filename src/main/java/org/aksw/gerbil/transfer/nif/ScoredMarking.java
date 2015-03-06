package org.aksw.gerbil.transfer.nif;

/**
 * A class implementing this interface contains a confidence score for this
 * Marking.
 * 
 * @author Michael Röder
 * 
 */
public interface ScoredMarking extends Marking {

    public double getConfidence();

    public void setConfidence(double confidence);

}
