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
