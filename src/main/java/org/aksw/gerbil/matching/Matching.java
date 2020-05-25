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

    public String getName() {
        return name();
    }
}