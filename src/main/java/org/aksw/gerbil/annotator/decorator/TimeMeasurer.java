package org.aksw.gerbil.annotator.decorator;

public interface TimeMeasurer {

	/**
	 * Returns the average runtime in milliseconds.
	 * 
	 * @return the average runtime in milliseconds or {@link Double#NaN} if
	 *         there are no measurements available.
	 */
	public double getAverageRuntime();

	public void reset();
}
