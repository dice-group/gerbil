package org.aksw.gerbil.qa.datatypes;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;

public class Relation implements Marking {

    protected Annotation subject;
    protected Property predicate;
    protected Annotation object;

    public Relation(Annotation subject, Property predicate, Annotation object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public Relation(Annotation subject, Property predicate) {
        this.subject = subject;
        this.predicate = predicate;
    }

    public Relation(Property predicate, Annotation object) {
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Relation other = (Relation) obj;
        if (object == null) {
            if (other.object != null)
                return false;
        } else if (!object.equals(other.object))
            return false;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        return true;
    }

    @Override
    public Object clone() {
        return new Relation(subject, predicate, object);
    }
}
