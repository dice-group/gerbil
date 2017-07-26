package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Relation;
import org.apache.jena.graph.Triple;

public class RelationImpl implements Relation {

    protected Triple relation;

    public RelationImpl(Triple relation) {
        setRelation(relation);
    }

    public RelationImpl(Relation relation) {
        this(relation.getRelation());
    }

    @Override
    public void setRelation(Triple relation) {
        this.relation = relation;
    }

    @Override
    public Triple getRelation() {
        return relation;
    }

    @Override
    public String toString() {
        return relation.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new RelationImpl(this);
    }

    @Override
    public int hashCode() {
        return ((relation == null) ? 0 : relation.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RelationImpl other = (RelationImpl) obj;
        if (relation == null) {
            if (other.relation != null)
                return false;
        } else if (!relation.equals(other.relation))
            return false;
        return true;
    }
}
