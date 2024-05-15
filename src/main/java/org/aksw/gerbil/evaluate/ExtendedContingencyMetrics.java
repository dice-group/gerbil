/**
 * Represents extended evaluation metrics of an entity based on a contingency matrix.
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
package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.matching.EvaluationCounts;

/**
 * Represents extended contingency metrics of an entity procured from experiment.
 */
public class ExtendedContingencyMetrics {

    /** The identifier of the entity. */
    private String id;

    /** Evaluation counts of Contingency Matrix for the entity. */
    private EvaluationCounts count;

    /** The precision score of the entity. */
    private double precision;

    /** The recall score of the entity. */
    private double recall;

    /** The F1 score of the entity. */
    private double f1Score;

    /**
     * Constructs an instance of ExtendedContingencyMetrics.
     *
     * @param id The identifier of the entity.
     * @param count Evaluation counts for the entity.
     * @param precision The precision score of the entity.
     * @param recall The recall score of the entity.
     * @param f1Score The F1 score of the entity.
     */
    public ExtendedContingencyMetrics(String id, EvaluationCounts count, double precision, double recall, double f1Score) {
        this.id = id;
        this.count = count;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
    }

    /**
     * Retrieves the identifier of the entity.
     * @return The identifier of the entity.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the evaluation counts for the entity.
     * @return Evaluation counts for the entity.
     */
    public EvaluationCounts getCount() {
        return count;
    }

    /**
     * Retrieves the precision score of the entity.
     * @return The precision score of the entity.
     */
    public double getPrecision() {
        return precision;
    }

    /**
     * Retrieves the recall score of the entity.
     * @return The recall score of the entity.
     */
    public double getRecall() {
        return recall;
    }

    /**
     * Retrieves the F1 score of the entity.
     * @return The F1 score of the entity.
     */
    public double getF1Score() {
        return f1Score;
    }
}
