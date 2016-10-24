/**
 * This file is part of NIF transfer library for the General Entity Annotator
 * Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.ScoredSpan;
import org.aksw.gerbil.transfer.nif.Span;

public class ScoredSpanImpl extends SpanImpl implements ScoredSpan {

	private double confidence;

	public ScoredSpanImpl(final int startPosition, final int length, final double confidence) {
		super(startPosition, length);
		this.confidence = confidence;
	}

	public ScoredSpanImpl(final Span span, final double confidence) {
		super(span);
		this.confidence = confidence;
	}

	public ScoredSpanImpl(final int startPosition, final int length, final double confidence, final boolean isWord) {
		super(startPosition, length, isWord);
		this.confidence = confidence;
	}

	public ScoredSpanImpl(final ScoredSpanImpl scoredSpanImpl) {
		this(scoredSpanImpl, scoredSpanImpl.getConfidence());
	}

	@Override
	public double getConfidence() {
		return confidence;
	}

	@Override
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp = Double.doubleToLongBits(confidence);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ScoredSpanImpl)) {
			return false;
		}
		ScoredSpanImpl other = (ScoredSpanImpl) obj;
		if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(startPosition);
		builder.append(", ");
		builder.append(length);
		builder.append(", ");
		builder.append(confidence);
		builder.append(')');
		return builder.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new ScoredSpanImpl(this);
	}

}
