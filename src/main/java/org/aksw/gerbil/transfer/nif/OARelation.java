package org.aksw.gerbil.transfer.nif;

import java.util.Set;


/**
 * Implements the Open Annotation Relation for the 
 * Knowledge and Relation Extraction Tasks
 *
 */
public interface OARelation extends Marking {

    public void setRelation(String subject, String predicate, String object);
    
    public Set<String>[] getRelation();
    
    public Set<String> getSubject();
    
    public Set<String> getPredicate();
    
    public Set<String> getObject();
}
