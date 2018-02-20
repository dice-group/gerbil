/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The type of an experiment.
 * 
 * @author m.roeder
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExperimentType implements Describable {

    /**
     * Annotate to KB
     * <p>
     * Identify the relevant mentions in the input text and assign to each of
     * them the pertinent entities. Entity Extraction comprises the two tasks
     * {@link #ERec} and {@link #D2KB} .
     * </p>
     * Input: text <br>
     * Output: marked entities and mentions for their meaning
     */
    A2KB("A2KB", "The annotator gets a text and shall recognize entities inside and link them to a knowledge base."),

    /**
     * Concepts to KB
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the
     * input text.
     * </p>
     * Input: text <br>
     * Output: marked entities
     */
    C2KB("C2KB", "The annotator gets a text and shall return relevant entities that are mentioned inside the text."),

    /**
     * Disambiguate to KB
     * <p>
     * D2KB is the assigning of a URI from a given Knowledge Base to a given
     * entity or an artificial generated URI if the entity is not present inside
     * the KB.
     * </p>
     * Input: text with marked entities <br>
     * Output: mentions for every entity
     */
    D2KB(
            "D2KB",
            "The input for the annotator is a text with entities that already have been marked inside. The annotator should link all these mentioned entities to a knowledge base."),

    /**
     * Scored - annotate to KB
     * <p>
     * Identify the relevant mentions in the input text and assign to each of
     * them the pertinent entities. Additionally, each annotation is assigned a
     * score representing the likelihood that the annotation is correct.
     * </p>
     * Input: text <br>
     * Output: marked entities and scored mentions for their meaning
     */
    @Deprecated
    Sa2KB(
            "Sa2KB",
            "The annotator gets a text and shall recognize entities inside and link them to a knowledge base. Additionally, each annotation is assigned a score representing the likelihood that the annotation is correct."),

    /**
     * Scored concepts to KB
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the
     * input text. Additionally, each tag is assigned a score representing the
     * likelihood that the annotation is correct.
     * </p>
     * Input: text <br>
     * Output: scored markings of entities
     */
    @Deprecated
    Sc2KB(
            "Sc2KB",
            "The annotator gets a text and shall return relevant entities that are mentioned inside the text. Additionally, each tag is assigned a score representing the likelihood that the annotation is correct."),

    /**
     * Ranked - concepts to KB
     * <p>
     * Identify the entities mentioned in a text and rank them in terms of their
     * relevance for the topics dealt with in the input text.
     * </p>
     * Input: text <br>
     * Output: ranked markings of entities
     */
    @Deprecated
    Rc2KB(
            "Sc2KB",
            "The annotator gets a text and shall return relevant entities that are mentioned inside the textand rank them in terms of their relevance for the topics dealt with in the input text"),

    /**
     * Entity Recognition is the identification of an entity inside a given
     * text.
     */
    ERec("Entity Recognition", "Entity Recognition is the identification of entities inside a given text."),

    /**
     * Entity Typing is the assigning of a class URI from a given Knowledge Base
     * to a given entity inside a given text.
     */
    ETyping("Entity Typing",
            "Entity Typing is the assigning of a class URI from a given Knowledge Base to a given entity inside a given text."),

    /**
     * This task comprises the recognition, linking and typing of all entities
     * inside a given text.
     */
    OKE_Task1("OKE Challenge 2015 - Task 1",
            "This task comprises the recognition, linking and typing of all entities inside a given text."),

    /**
     * This task comprises the determining of the type of a given entity inside
     * a given text and the extraction of the part of the text, describing the
     * type.
     */
    OKE_Task2(
            "OKE Challenge 2015 - Task 2",
            "This task comprises the determining of the type of a given entity inside a given text and the extraction of the part of the text, describing the type."),

    /**
     * The annotator gets a text and shall recognize entities inside and their
     * types.
     */
    RT2KB("RT2KB", "The annotator gets a text and shall recognize entities inside and their types."),
    		
    /**
     * The annotator gets a text and several already linked entities and shall recognize their relationship inside the text.
     */
    RE("RE", "The annotator gets a text and several already linked entities and shall recognize their relationship inside the text."),
    
    /**
     * The annotator gets a text and shall recognize entities inside and the relationship between them.
     */
    OKE2018Task4("OKE2018Task4", "The annotator gets a text and shall recognize entities inside and the relationship between them.")
    ;

    private String label;
    private String description;

    ExperimentType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name();
    }

    public boolean equalsOrContainsType(ExperimentType type) {
        switch (this) {
        case Sa2KB: // falls through
        case A2KB: {
            switch (type) {
            case C2KB: // falls through
            case A2KB:
            case D2KB:
            case ERec:
            case Sa2KB:
            case Sc2KB:
            case Rc2KB: {
                return true;
            }
            case ETyping: // falls through
            case RT2KB:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            }
        }
        case Sc2KB: {
            switch (type) {
            case ERec: // falls through
            case Sa2KB:
            case A2KB:
            case D2KB:
            case ETyping:
            case RT2KB:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case Sc2KB: // falls through
            case Rc2KB:
            case C2KB: {
                return true;
            }
            }
        }
        case Rc2KB: {
            switch (type) {
            case ERec: // falls through
            case Sa2KB:
            case Sc2KB:
            case A2KB:
            case D2KB:
            case RT2KB:
            case ETyping:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case Rc2KB: // falls through
            case C2KB: {
                return true;
            }
            }
        }
        case RT2KB: {
            switch (type) {
            case ERec: // falls through
            case ETyping:
            case RT2KB: {
                return true;
            }
            case C2KB: // falls through
            case A2KB:
            case D2KB:
            case Sa2KB:
            case Sc2KB:
            case Rc2KB:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            }
        }
        case C2KB: {
            return type == C2KB;
        }
        case D2KB: {
            return type == D2KB;
        }
        case ERec: {
            return type == ERec;
        }
        case ETyping: {
            return type == ETyping;
        }
        case OKE_Task1: {
            switch (type) {
            case OKE_Task2: {
                return false;
            }
            default: {
                return true;
            }
            }
        }
        case OKE_Task2: {
            return type == OKE_Task2;
        }
        case RE:{
        	return type == RE;
        }
        case OKE2018Task4:{
        	switch(type) {
        	case RE:
        	case OKE2018Task4:
        	case ERec:
        		return true;
        	}
        }
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{name:");
        builder.append(name());
        builder.append(", label:");
        builder.append(getLabel());
        builder.append(", description:");
        builder.append(getDescription());
        builder.append('}');
        return builder.toString();
    }
}
