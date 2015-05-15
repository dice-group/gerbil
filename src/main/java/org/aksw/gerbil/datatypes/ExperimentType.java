/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The type of an experiment.
 * 
 * old hierarchy of experiment types:<br>
 * {@link #Sa2KB} ≻ {@link #Sc2KB}<br>
 * {@link #Sc2KB} ≻ {@link #Rc2KB}<br>
 * {@link #Rc2KB} ≻ {@link #C2KB}<br>
 * {@link #Sa2KB} ≻ {@link #A2KB}<br>
 * {@link #A2KB} ≻ {@link #C2KB}<br>
 * {@link #A2KB} ≻ {@link #D2KB}<br>
 * 
 * 
 * 
 * <p>
 * {@link #Sa2KB} is the hardest problem containing all others while
 * {@link #C2KB} and {@link #D2KB} are the leaves of the hierarchy.
 * </p>
 * 
 * @author m.roeder
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExperimentType implements Describable {
    /**
     * Disambiguate to Wikipedia
     * <p>
     * Assign to each input mention its pertinent entity (possibly null).
     * </p>
     * Input: text with marked entities <br>
     * Output: mentions for every entity
     */
    @Deprecated
    D2KB(
            "D2KB",
            "The input for the annotator is a text with entities that already have been marked inside. The annotator should link all these mentioned entities to a knowledge base."),
    /**
     * Annotate to Wikipedia
     * <p>
     * Identify the relevant mentions in the input text and assign to each of
     * them the pertinent entities.
     * </p>
     * Input: text<br>
     * Output: marked entities and mentions for their meaning
     */
    @Deprecated
    A2KB("A2KB", "The annotator gets a text and shall recognize entities inside and link them to a knowledge base."),
    /**
     * Scored-annotate to Wikipedia
     * <p>
     * Identify the relevant mentions in the input text and assign to each of
     * them the pertinent entities. Additionally, each annotation is assigned a
     * score representing the likelihood that the annotation is correct.
     * </p>
     * Input: text<br>
     * Output: marked entities and scored mentions for their meaning
     */
    @Deprecated
    Sa2KB(
            "Sa2KB",
            "The annotator gets a text and shall recognize entities inside and link them to a knowledge base. Additionally, each annotation is assigned a score representing the likelihood that the annotation is correct."),
    /**
     * Concepts to Wikipedia
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the
     * input text.
     * </p>
     * Input: text<br>
     * Output: marked entities
     */
    @Deprecated
    C2KB("C2KB", "The annotator gets a text and shall return relevant entities that are mentioned inside the text."),
    /**
     * Scored concepts to Wikipedia
     * <p>
     * Tags are taken as the set of relevant entities that are mentioned in the
     * input text. Additionally, each tag is assigned a score representing the
     * likelihood that the annotation is correct.
     * </p>
     * Input: text<br>
     * Output: scored markings of entities
     */
    @Deprecated
    Sc2KB(
            "Sc2KB",
            "The annotator gets a text and shall return relevant entities that are mentioned inside the text. Additionally, each tag is assigned a score representing the likelihood that the annotation is correct."),
    /**
     * Ranked-concepts to Wikipedia
     * <p>
     * Identify the entities mentioned in a text and rank them in terms of their
     * relevance for the topics dealt with in the input text.
     * </p>
     * Input: text<br>
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
     * Entity Linking is the assigning of a URI from a given Knowledge Base to a
     * given entity.
     */
    ELink("Entity Linking",
            "Entity Linking is the assigning of a URI from a given Knowledge Base to a given entity inside a given text."),
    /**
     * Entity Extraction comprises the two tasks {@link #EntityRecognition} and
     * {@link #EntityLinking}.
     */
    EExt("Entity Extraction", "Entity Extraction comprises the two steps Entity Recognition and Entity Linking."), ETyping(
            "Entity Typing",
            "Entity Typing is the assigning of a class URI from a given Knowledge Base to a given entity inside a given text."), OKE_Task1(
            "OKE Challenge 2015 - Task 1",
            "This task comprises the recognition, linking and typing of all entities inside a given text."), OKE_Task2(
            "OKE Challenge 2015 - Task 2",
            "This task comprises the determining of the type of a given entity inside a given text and the extraction of the part of the text, describing the type."), ;

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

    // public Class<? extends Marking> getInputType() {
    // return null;
    // }
    //
    // public Class<? extends Marking> getOutputType() {
    // return null;
    // }

    public boolean equalsOrContainsType(ExperimentType type) {
        switch (this) {
        case Sa2KB: {
            return true;
        }
        case Sc2KB: {
            switch (type) {
            case ERec:
            case Sa2KB: // falls through
            case EExt:
            case A2KB:
            case ELink:
            case D2KB:
            case ETyping:
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
            case ERec:
            case Sa2KB: // falls through
            case Sc2KB:
            case EExt:
            case A2KB:
            case ELink:
            case D2KB:
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
        case EExt: // falls through
        case A2KB: {
            switch (type) {
            case Sa2KB: // falls through
            case Sc2KB:
            case Rc2KB:
            case ETyping:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case A2KB: // falls through
            case EExt:
            case ELink:
            case ERec:
            case C2KB:
            case D2KB: {
                return true;
            }
            }
        }
        case C2KB: {
            switch (type) {
            case ERec:
            case Sa2KB: // falls through
            case Sc2KB:
            case Rc2KB:
            case EExt:
            case A2KB:
            case ELink:
            case D2KB:
            case ETyping:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case C2KB: {
                return true;
            }
            }
        }
        case ELink:// falls through
        case D2KB: {
            switch (type) {
            case ERec:
            case Sa2KB: // falls through
            case Sc2KB:
            case Rc2KB:
            case EExt:
            case A2KB:
            case C2KB:
            case ETyping:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case ELink:
            case D2KB: {
                return true;
            }
            }
        }
        case ERec: {
            switch (type) {
            case Sa2KB: // falls through
            case Sc2KB:
            case Rc2KB:
            case EExt:
            case A2KB:
            case C2KB:
            case ELink:
            case D2KB:
            case ETyping:
            case OKE_Task1:
            case OKE_Task2: {
                return false;
            }
            case ERec: {
                return true;
            }
            }
        }
        case OKE_Task1: {
            return true;
        }
        case ETyping: {
            return type == ETyping;
        }
        case OKE_Task2: {
            return type == OKE_Task2;
        }
        }
        return false;
    }

    public String toString() {
        return "{label: " + getLabel() + ", description: " + getDescription() + "}";
    }
}
