package org.aksw.gerbil.transfer.nif;

import java.util.List;

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

    /**
     * Returns {@code true} if the given {@link Relation} instances have equal
     * subjects, predicates and objects.
     * 
     * @param r1
     *            an instance of {@link Relation} that should be checked for its
     *            equality with the second instance based on the semantics of the
     *            {@link Relation} interface.
     * @param r2
     *            an instance of {@link Relation} that should be checked for its
     *            equality with the first instance based on the semantics of the
     *            {@link Relation} interface.
     * @return {@code true} if the given {@link Relation} instances have equal
     *         subjects, predicates and objects, else {@code false}.
     */
    public static boolean equals(Relation r1, Relation r2) {
        Meaning m1, m2;
        m1 = r1.getSubject();
        m2 = r2.getSubject();
        if (((m1 == null) && (m2 == null)) || ((m1 != null) && (m2 != null) && Meaning.equals(m1, m2))) {
            m1 = r1.getPredicate();
            m2 = r2.getPredicate();
            if (((m1 == null) && (m2 == null)) || ((m1 != null) && (m2 != null) && Meaning.equals(m1, m2))) {
                m1 = r1.getObject();
                m2 = r2.getObject();
                return ((m1 == null) && (m2 == null)) || ((m1 != null) && (m2 != null) && Meaning.equals(m1, m2));
            }
        }
        return false;
    }
}
