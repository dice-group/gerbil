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
package org.aksw.gerbil.utils.filter;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;

/**
 * Filters markings based on their confidence score. To pass the filter a given
 * {@link ScoredMarking} must have a confidence score that is higher than the
 * given {@link #threshold}. If a {@link Marking} does not implement the
 * {@link ScoredMarking} interface, it will always pass this filter.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 *            the class of the filtered markings
 */
public class ConfidenceScoreBasedMarkingFilter<T extends Marking> extends AbstractMarkingFilter<T> {

    /**
     * The threshold used for the filtering.
     */
    private double threshold;

    /**
     * Constructor.
     * 
     * @param threshold
     *            The threshold used for the filtering
     */
    public ConfidenceScoreBasedMarkingFilter(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isMarkingGood(T marking) {
        if (marking instanceof ScoredMarking) {
            return ((ScoredMarking) marking).getConfidence() > threshold;
        } else {
            return true;
        }
    }

    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * @param threshold
     *            the threshold to set
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

}
