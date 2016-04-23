package org.aksw.gerbil.qa.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.datatypes.marking.MeaningsContainingMarking;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;

/**
 * This class represents a triple containing subject, predicate and object. Note
 * that the subject and the object can be <code>null</code> which indicates that
 * it is a variable. The object can be a literal, too.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class Relation implements Marking, MeaningsContainingMarking {

    protected Annotation subject;
    protected Property predicate;
    protected Annotation object;
    protected String objectLiteral;

    protected Relation(Annotation subject, Property predicate, Annotation object, String objectLiteral) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.objectLiteral = objectLiteral;
    }

    public Relation(Annotation subject, Property predicate, Annotation object) {
        this(subject, predicate, object, null);
    }

    public Relation(Annotation subject, Property predicate, String objectLiteral) {
        this(subject, predicate, null, objectLiteral);
    }

    public Relation(Annotation subject, Property predicate) {
        this(subject, predicate, null, null);
    }

    public Relation(Property predicate, Annotation object) {
        this(null, predicate, object, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((objectLiteral == null) ? 0 : objectLiteral.hashCode());
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
        if (objectLiteral == null) {
            if (other.objectLiteral != null)
                return false;
        } else if (!objectLiteral.equals(other.objectLiteral))
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

    @Override
    public Collection<? extends Meaning> getMeanings() {
        List<Meaning> meanings = new ArrayList<Meaning>(3);
        if (subject != null) {
            meanings.add(subject);
        }
        if (predicate != null) {
            meanings.add(predicate);
        }
        if (object != null) {
            meanings.add(object);
        }
        return meanings;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Relation [");
        builder.append(subject);
        builder.append(", ");
        builder.append(predicate);
        if (object != null) {
            builder.append(", ");
            builder.append(object);
            builder.append("]");
        } else {
            builder.append(", \"");
            builder.append(objectLiteral);
            builder.append("\"]");
        }
        return builder.toString();
    }
}
