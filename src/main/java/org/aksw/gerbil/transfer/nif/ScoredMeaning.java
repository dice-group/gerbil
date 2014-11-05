package org.aksw.gerbil.transfer.nif;

/**
 * A class implementing this interface contains a URI that points to the meaning
 * of this object.
 * 
 * @author Michael Röder
 * 
 */
public interface ScoredMeaning extends Meaning {


    public double getConfidence();

    public void setConfidence(double confidence);
}
