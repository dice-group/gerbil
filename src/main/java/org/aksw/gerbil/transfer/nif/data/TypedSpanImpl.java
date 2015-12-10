/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.transfer.nif.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.TypedSpan;

public class TypedSpanImpl extends SpanImpl implements TypedSpan {

    protected Set<String> types = new HashSet<String>();

    public TypedSpanImpl(int startPosition, int length, Set<String> types) {
        super(startPosition, length);
        setTypes(types);
    }

    public TypedSpanImpl(int startPosition, int length, String... types) {
        super(startPosition, length);
        setTypes(types);
    }

    public TypedSpanImpl(TypedSpanImpl typedSpanImpl) {
        super(typedSpanImpl);
        setTypes(typedSpanImpl.getTypes());
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(String... types) {
        this.types.clear();
        for (int i = 0; i < types.length; ++i) {
            this.types.add(types[i]);
        }
    }

    public void setTypes(Set<String> types) {
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
        builder.append(", a ");
        builder.append(Arrays.toString(types.toArray()));
        builder.append(')');
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypedSpanImpl other = (TypedSpanImpl) obj;
        if (types == null) {
            if (other.types != null)
                return false;
        } else if (!types.equals(other.types))
            return false;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new TypedSpanImpl(this);
    }
}
