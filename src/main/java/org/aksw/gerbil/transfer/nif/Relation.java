package org.aksw.gerbil.transfer.nif;

import org.apache.jena.graph.Triple;

public interface Relation extends Marking {

    public void setRelation(Triple relation);
    
    public Triple getRelation();
}
