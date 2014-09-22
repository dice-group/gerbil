package org.aksw.gerbil.matching;

/**
 * The matching defines how the results of an annotator are compared with the annotations of the dataset.
 * 
 * @author m.roeder
 * 
 */
public enum Matching {
    /**
     * The matching returns true iff the disambiguated entity of the annotater equals at least one annotation of the
     * text.
     */
    STRONG_ENTITY_MATCH,
    /**
     * This matching returns true iff a) the position, b) the length and c) the disambiguated meaning of an entity are
     * the same as those of the entity in the dataset.
     */
    STRONG_ANNOTATION_MATCH,
    /**
     * This matching returns true iff the disambiguated meaning of an entity is the same as the meaning of an entity in
     * the dataset and both entities overlap inside the text.
     */
    WEAK_ANNOTATION_MATCH
}
