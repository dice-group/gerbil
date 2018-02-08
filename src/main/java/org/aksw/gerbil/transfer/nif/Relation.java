package org.aksw.gerbil.transfer.nif;

import java.util.List;
import java.util.Set;


/**
 * Implements a Relation Object 
 *
 */
public interface Relation extends Marking {

	public void setRelation(Meaning subject, Meaning predicate, Meaning object);
    
    public List<Meaning> getRelation();
    
    public Meaning getSubject();
    
    public Meaning getPredicate();
    
    public Meaning getObject();
}
