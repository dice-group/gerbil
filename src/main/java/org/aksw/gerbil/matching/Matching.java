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
package org.aksw.gerbil.matching;

import org.aksw.gerbil.datatypes.Describable;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The matching defines how the results of an annotator are compared with the
 * annotations of the dataset.
 * 
 * @author m.roeder
 * 
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Matching implements Describable {
    /**
     * The matching returns true iff the disambiguated entity of the annotater
     * equals at least one tag/annotation of the text.
     */
    STRONG_ENTITY_MATCH("Me - strong entity match",
            "The URI returned by the annotator has to equal an entity or tag URI of the gold standard."),
    /**
     * This matching returns true iff a) the position, b) the length and c) the
     * disambiguated meaning of an entity are the same as those of the entity in
     * the dataset.
     */
    STRONG_ANNOTATION_MATCH("Ma - strong annotation match",
            "The position of the entity inside the text has to exactly match the position inside the gold standard."),
    /**
     * This matching returns true iff the disambiguated meaning of an entity is
     * the same as the meaning of an entity in the dataset and both entities
     * overlap inside the text.
     */
    WEAK_ANNOTATION_MATCH("Mw - weak annotation match",
            "The position of the entity inside the text has to overlap the position inside the gold standard.");

    private String label;
    private String description;

    Matching(String label, String description) {
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
}
