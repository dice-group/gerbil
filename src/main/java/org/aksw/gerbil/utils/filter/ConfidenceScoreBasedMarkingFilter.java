package org.aksw.gerbil.utils.filter;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;

/**
 * Filters markings based on their confidence score. To pass the filter a given
 * {@link ScoredMarking} must have a confidence score that is higher or equal
 * the given {@link #threshold}. If a {@link Marking} does not implement the
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
			return ((ScoredMarking) marking).getConfidence() >= threshold;
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
