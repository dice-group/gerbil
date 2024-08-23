package org.aksw.gerbil.transfer.nif;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.RelationImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredAnnotation;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.aksw.gerbil.transfer.nif.data.ScoredRelationImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredSpanImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredTypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkingBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkingBuilder.class);

    protected Set<String> meanings;
    protected Set<String> types;
    protected int start = -1;
    protected int length = -1;
    protected int end = -1;
    protected double confidence;
    protected boolean hasConfidence = false;
    protected ProvenanceInfo provenance = null;
    protected Set<String> subject = null;
    protected Set<String> predicate = null;
    protected Set<String> object = null;

    public void clear() {
        meanings = null;
        types = null;
        start = -1;
        length = -1;
        end = -1;
        hasConfidence = false;
        provenance = null;
        subject = null;
        predicate = null;
        object = null;
    }

    /**
     * @return the meanings
     */
    public Set<String> getMeanings() {
        return meanings;
    }

    /**
     * @param meanings the meanings to set
     */
    public void setMeanings(Set<String> meanings) {
        this.meanings = meanings;
    }

    /**
     * @param meaning the meaning to be added
     */
    public void addMeaning(String meaning) {
        if (meanings == null) {
            meanings = new HashSet<>();
        }
        meanings.add(meaning);
    }

    /**
     * @return the types
     */
    public Set<String> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Set<String> types) {
        this.types = types;
    }

    /**
     * @param meanings the type to be added
     */
    public void addType(String type) {
        if (types == null) {
            types = new HashSet<>();
        }
        types.add(type);
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @return the hasConfidence flag
     */
    public boolean hasConfidence() {
        return hasConfidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(double confidence) {
        this.hasConfidence = true;
        this.confidence = confidence;
    }

    /**
     * @return the provenance
     */
    public ProvenanceInfo getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(ProvenanceInfo provenance) {
        this.provenance = provenance;
    }

    /**
     * @return the subject
     */
    public Set<String> getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(Set<String> subject) {
        this.subject = subject;
    }

    /**
     * @param subject the subject to be added
     */
    public void addSubject(String subject) {
        if (this.subject == null) {
            this.subject = new HashSet<>();
        }
        this.subject.add(subject);
    }

    /**
     * @return the predicate
     */
    public Set<String> getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(Set<String> predicate) {
        this.predicate = predicate;
    }

    /**
     * @param predicate the predicate to be added
     */
    public void addPredicate(String predicate) {
        if (this.predicate == null) {
            this.predicate = new HashSet<>();
        }
        this.predicate.add(predicate);
    }

    /**
     * @return the object
     */
    public Set<String> getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Set<String> object) {
        this.object = object;
    }

    /**
     * @param object the object to be added
     */
    public void addObject(String object) {
        if (this.object == null) {
            this.object = new HashSet<>();
        }
        this.object.add(object);
    }

    public Marking build() {
        Marking createdMarking = null;
        // If this is a relation
        if ((subject != null) && (predicate != null) && (object != null)) {
            createdMarking = buildRelation();
        }
        // If this marking has a meaning
        if (meanings != null) {
            // If there is a type
            if (types != null) {
                if ((start >= 0) && ((length >= 0) || (end >= 0))) {
                    // Calculate length if not present
                    if (length < 0) {
                        length = end - start;
                    }
                    if (hasConfidence) {
                        createdMarking = new ScoredTypedNamedEntity(start, length, meanings, types, confidence);
                    } else {
                        createdMarking = new TypedNamedEntity(start, length, meanings, types);
                    }
                } else {
                    // We don't have this type of marking
                    throw new NotImplementedException(
                            "There is no implementation for a typed meaning without positional information.");
                }
            } else {
                if ((start >= 0) && ((length >= 0) || (end >= 0))) {
                    // Calculate length if not present
                    if (length < 0) {
                        length = end - start;
                    }
                    if (hasConfidence) {
                        createdMarking = new ScoredNamedEntity(start, length, meanings, confidence);
                    } else {
                        createdMarking = new NamedEntity(start, length, meanings);
                    }
                } else {
                    if (hasConfidence) {
                        createdMarking = new ScoredAnnotation(meanings, confidence);
                    } else {
                        createdMarking = new Annotation(meanings);
                    }
                }
            } // has type
        } else {
            // There is no meaning
            if (types != null) {
                if ((start >= 0) && ((length >= 0) || (end >= 0))) {
                    // Calculate length if not present
                    if (length < 0) {
                        length = end - start;
                    }
                    if (hasConfidence) {
                        // We don't have this type of marking
                        throw new NotImplementedException("There is no implementation for a ScoredTypedSpan.");
                    } else {
                        createdMarking = new TypedSpanImpl(start, length, types);
                    }
                } else {
                    // We don't have this type of marking
                    throw new NotImplementedException(
                            "There is no implementation for a typed marking without positional information.");
                }
            } else {
                if ((start >= 0) && ((length >= 0) || (end >= 0))) {
                    // Calculate length if not present
                    if (length < 0) {
                        length = end - start;
                    }
                    if (hasConfidence) {
                        createdMarking = new ScoredSpanImpl(start, length, confidence);
                    } else {
                        createdMarking = new SpanImpl(start, length);
                    }
                } else {
                    LOGGER.warn("Not enough information to create a Marking. Returning null.");
                }
            } // has type
        } // has meaning
          // If we have any provenance information
        if ((createdMarking != null) && (provenance != null)) {
            createdMarking.setProvenanceInfo(provenance);
        }
        return createdMarking;
    }

    protected Relation buildRelation() {
        if (hasConfidence) {
            return new ScoredRelationImpl(new Annotation(subject), new Annotation(predicate), new Annotation(object),
                    confidence);
        } else {
            return new RelationImpl(new Annotation(subject), new Annotation(predicate), new Annotation(object));
        }
    }
}
