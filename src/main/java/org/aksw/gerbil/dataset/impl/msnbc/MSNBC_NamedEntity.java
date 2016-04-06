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
package org.aksw.gerbil.dataset.impl.msnbc;

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class MSNBC_NamedEntity extends NamedEntity {

    private static final int NOT_SET_SENTINEL = -1;

    protected String surfaceForm = null;

    public MSNBC_NamedEntity() {
        super(NOT_SET_SENTINEL, NOT_SET_SENTINEL, new HashSet<String>());
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public void setSurfaceForm(String surfaceForm) {
        this.surfaceForm = surfaceForm;
    }

    public boolean isComplete() {
        return (this.startPosition != NOT_SET_SENTINEL) && (this.length != NOT_SET_SENTINEL) && (!this.uris.isEmpty())
                && (this.surfaceForm != null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        builder.append(startPosition);
        builder.append(", ");
        builder.append(length);
        builder.append(", \"");
        builder.append(surfaceForm);
        builder.append("\", ");
        builder.append(Arrays.toString(uris.toArray()));
        builder.append(')');
        return builder.toString();
    }

    public NamedEntity toNamedEntity() {
        return new NamedEntity(startPosition, length, uris);
    }
}
