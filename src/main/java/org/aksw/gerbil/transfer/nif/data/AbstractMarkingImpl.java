package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ProvenanceInfo;

public abstract class AbstractMarkingImpl implements Marking {
    
    private ProvenanceInfo provencance;

    @Override
    public void setProvenanceInfo(ProvenanceInfo provencance) {
        this.provencance = provencance;
    }

    @Override
    public ProvenanceInfo getProvenanceInfo() {
        return provencance;
    }
    
    public abstract Object clone() throws CloneNotSupportedException;

}
