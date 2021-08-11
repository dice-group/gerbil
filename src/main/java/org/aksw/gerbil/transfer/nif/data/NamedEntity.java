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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.MeaningEqualityChecker;
import org.aksw.gerbil.transfer.nif.MeaningSpan;

public class NamedEntity extends SpanImpl implements MeaningSpan {

	@Deprecated
	protected String uri;
	protected Set<String> uris = new LinkedHashSet<>();

	public NamedEntity(final int startPosition, final int length, final String uri) {
		super(startPosition, length);
		this.uri = uri;
		this.uris.add(uri);
	}

	public NamedEntity(final int startPosition, final int length, final String uri, final boolean isWord) {
		super(startPosition, length, isWord);
		this.uri = uri;
		this.uris.add(uri);
	}

	public NamedEntity(final int startPosition, final int length, final Set<String> uris) {
		super(startPosition, length);
		setUris(uris);
	}

	public NamedEntity(final int startPosition, final int length, final Set<String> uris, final boolean isWord) {
		super(startPosition, length, isWord);
		setUris(uris);
	}

	public NamedEntity(final NamedEntity namedEntity) {
		super(namedEntity);
		setUris(namedEntity.getUris());
	}

	@Deprecated
	@Override
	public String getUri() {
		return uri;
	}

	@Deprecated
	@Override
	public void setUri(final String uri) {
		this.uri = uri;
		this.uris.clear();
		this.uris.add(uri);
	}

	@Override
	public Set<String> getUris() {
		return uris;
	}

	@Override
	public void setUris(final Set<String> uris) {
		this.uris = uris;
		if (uris.size() > 0) {
			this.uri = uris.iterator().next();
		} else {
			this.uri = null;
		}
	}

	@Override
	public void addUri(final String uri) {
		this.uris.add(uri);
		if (this.uri == null) {
			this.uri = uri;
		}
	}

	@Override
	public boolean containsUri(final String uri) {
		return uris.contains(uri);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((uris == null) ? 0 : uris.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		NamedEntity other = (NamedEntity) obj;
		if (uris == null) {
			if (other.uris != null) {
				return false;
			}
		} else if (!MeaningEqualityChecker.overlaps(this, other)) {
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
		builder.append(Arrays.toString(uris.toArray()));
		builder.append(')');
		return builder.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new NamedEntity(this);
	}
}
