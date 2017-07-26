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

import org.aksw.gerbil.transfer.nif.Span;

public class SpanImpl extends AbstractMarkingImpl implements Span {

	protected int startPosition;
	protected int length;
	protected boolean isWord;

	public SpanImpl(final int startPosition, final int length) {
		this.startPosition = startPosition;
		this.length = length;
		this.isWord = false;
	}

	public SpanImpl(final int startPosition, final int length, final boolean isWord) {
		this.startPosition = startPosition;
		this.length = length;
		this.isWord = isWord;
	}

	public SpanImpl(final Span span) {
		this(span.getStartPosition(), span.getLength());
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void setStartPosition(final int startPosition) {
		this.startPosition = startPosition;
	}

	@Override
	public void setLength(final int length) {
		this.length = length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + length;
		result = (prime * result) + startPosition;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SpanImpl other = (SpanImpl) obj;
		if (length != other.length) {
			return false;
		}
		if (startPosition != other.startPosition) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + startPosition + ", " + length + ")";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new SpanImpl(this);
	}

	@Override
	public void setIsWord(final boolean isWord) {
		this.isWord = isWord;

	}

	@Override
	public boolean getIsWord() {
		return this.isWord;
	}
}
