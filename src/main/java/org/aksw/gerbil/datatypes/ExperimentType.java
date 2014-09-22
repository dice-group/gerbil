package org.aksw.gerbil.datatypes;

/**
 * The type of an experiment.
 * 
 * Hierarchy of experiment types:<br>
 * {@link #Sa2W} ≻ {@link #Sc2W}<br>
 * {@link #Sc2W} ≻ {@link #Rc2W}<br>
 * {@link #Rc2W} ≻ {@link #C2W}<br>
 * {@link #Sa2W} ≻ {@link #A2W}<br>
 * {@link #A2W} ≻ {@link #C2W}<br>
 * {@link #A2W} ≻ {@link #D2W}<br>
 * 
 * <p>
 * {@link #Sa2W} is the hardest problem containing all others while {@link #C2W} and {@link #D2W} are the leaves of the
 * hierarchy.
 * </p>
 * 
 * @author m.roeder
 * 
 */
public enum ExperimentType {
    /**
     * Disambiguate to Wikipedia
     * <p>
     * Assign to each input mention its pertinent entity (possibly null).
     * </p>
     * Input: text with marked entities <br>
     * Output: mentions for every entity
     */
    D2W,
    /**
     * Annotate to Wikipedia
     * <p>
     * Identify the relevant mentions in the input text and assign to each of them the pertinent entities.
     * </p>
     * Input: text<br>
     * Output: marked entities and mentions for their meaning
     */
    A2W,
    /**
     * Scored-annotate to Wikipedia
     * <p>
     * Identify the relevant mentions in the input text and assign to each of them the pertinent entities. Additionally,
     * each annotation is assigned a score representing the likelihood that the annotation is correct.
     * </p>
     * Input: text<br>
     * Output: marked entities and scored mentions for their meaning
     */
    Sa2W,
    /**
     * Concepts to Wikipedia
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the input text.
     * </p>
     * Input: text<br>
     * Output: marked entities
     */
    C2W,
    /**
     * Scored concepts to Wikipedia
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the input text. Additionally, each tag is
     * assigned a score representing the likelihood that the annotation is correct.
     * </p>
     * Input: text<br>
     * Output: scored markings of entities
     */
    Sc2W,
    /**
     * Ranked-concepts to Wikipedia
     * <p>
     * Identify the entities mentioned in a text and rank them in terms of their relevance for the topics dealt with in
     * the input text.
     * </p>
     * Input: text<br>
     * Output: ranked markings of entities
     */
    Rc2W;

    public boolean equalsOrContainsType(ExperimentType type) {
        switch (this) {
        case Sa2W: {
            return true;
        }
        case Sc2W: {
            switch (type) {
            case Sa2W: // falls through
            case A2W:
            case D2W: {
                return false;
            }
            case Sc2W: // falls through
            case Rc2W:
            case C2W: {
                return true;
            }
            }
        }
        case Rc2W: {
            switch (type) {
            case Sa2W: // falls through
            case Sc2W:
            case A2W:
            case D2W: {
                return false;
            }
            case Rc2W: // falls through
            case C2W: {
                return true;
            }
            }
        }
        case A2W: {
            switch (type) {
            case Sa2W: // falls through
            case Sc2W:
            case Rc2W:
            case C2W: {
                return false;
            }
            case A2W: // falls through
            case D2W: {
                return true;
            }
            }
        }
        case C2W: {
            switch (type) {
            case Sa2W: // falls through
            case Sc2W:
            case Rc2W:
            case A2W:
            case D2W: {
                return false;
            }
            case C2W: {
                return true;
            }
            }
        }
        case D2W: {
            switch (type) {
            case Sa2W: // falls through
            case Sc2W:
            case Rc2W:
            case A2W:
            case C2W: {
                return false;
            }
            case D2W: {
                return true;
            }
            }
        }
        }
        return false;
    }
}
