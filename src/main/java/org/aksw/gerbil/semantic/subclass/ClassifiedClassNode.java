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
package org.aksw.gerbil.semantic.subclass;

import java.util.Arrays;
import java.util.Set;

import com.carrotsearch.hppc.IntOpenHashSet;

public class ClassifiedClassNode extends SimpleClassNode {

    private IntOpenHashSet classIds = new IntOpenHashSet(2);

    public ClassifiedClassNode(String uri) {
        super(uri);
    }

    public ClassifiedClassNode(Set<String> uris/*
                                                * , List<SimpleClassNode>
                                                * children
                                                */) {
        super(uris/* , children */);
    }

    public IntOpenHashSet getClassIds() {
        return classIds;
    }

    public void setClassIds(IntOpenHashSet classIds) {
        this.classIds = classIds;
    }

    public void addClassId(int classId) {
        this.classIds.add(classId);
    }

    @Override
    public String toString() {
        return "(uris=" + Arrays.toString(uris.toArray(new String[uris.size()])) + ",classId=" + classIds.toString()
                + ")";
    }
}
