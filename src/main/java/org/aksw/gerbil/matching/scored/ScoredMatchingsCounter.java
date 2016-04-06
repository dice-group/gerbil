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
package org.aksw.gerbil.matching.scored;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

/**
 * <p>
 * Classes implementing this interface work like a MatchingsCounter but take
 * care of confidence scores inside the annotator results and, thus, returns an
 * array of different results for different confidence score thresholds.
 * </p>
 * <p>
 * Note that such an implementation should be able to take care of unscored
 * {@link Marking}s as well.
 * </p>
 * <p>
 * Note that a confidence score thresholds means the a confidence score has to
 * be higher than this score and that annotations with a score equal to the
 * threshold have been removed for the evaluation.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 */
public interface ScoredMatchingsCounter<T extends Marking> {

    public ScoredEvaluationCounts[] countMatchings(List<T> annotatorResult, List<T> goldStandard);
}
