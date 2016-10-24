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
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypedSpan;

public class TypedNamedEntity extends NamedEntity implements TypedSpan {

	protected Set<String> types = new HashSet<>();

	public TypedNamedEntity(final int startPosition, final int length, final String uri, final Set<String> types) {
		super(startPosition, length, uri);
		setTypes(types);
	}

	public TypedNamedEntity(final int startPosition, final int length, final Set<String> uris, final Set<String> types) {
		super(startPosition, length, uris);
		setTypes(types);
	}

	public TypedNamedEntity(final int startPosition, final int length, final String uri, final Set<String> types, final boolean isWord) {
		super(startPosition, length, uri, isWord);
		setTypes(types);
	}

	public TypedNamedEntity(final int startPosition, final int length, final Set<String> uris, final Set<String> types, final boolean isWord) {
		super(startPosition, length, uris, isWord);
		setTypes(types);
	}

	public TypedNamedEntity(final TypedNamedEntity typedNamedEntity) {
		super(typedNamedEntity);
		setTypes(types);
	}

	@Override
	public Set<String> getTypes() {
		return types;
	}

	@Override
	public void setTypes(final Set<String> types) {
		this.types.clear();
		this.types.addAll(types);
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
		builder.append(", a ");
		builder.append(Arrays.toString(types.toArray()));
		builder.append(')');
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((types == null) ? 0 : types.hashCode());
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
		TypedNamedEntity other = (TypedNamedEntity) obj;
		if (types == null) {
			if (other.types != null) {
				return false;
			}
		} else if (!types.equals(other.types)) {
			return false;
		}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new TypedNamedEntity(this);
	}

}
