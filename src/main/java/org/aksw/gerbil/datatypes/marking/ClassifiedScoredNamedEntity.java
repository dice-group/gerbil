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
package org.aksw.gerbil.datatypes.marking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;

import com.carrotsearch.hppc.BitSet;

public class ClassifiedScoredNamedEntity extends ScoredNamedEntity
        implements ClassifiedSpanMeaning {

    protected BitSet classBits = new BitSet(MarkingClasses.NUMBER_OF_CLASSES);

    public ClassifiedScoredNamedEntity(int startPosition, int length, Set<String> uris, double confidence) {
        super(startPosition, length, uris, confidence);
    }

    @Override
    public List<MarkingClasses> getClasses() {
        List<MarkingClasses> classes = new ArrayList<MarkingClasses>();
        for (int i = 0; i < MarkingClasses.NUMBER_OF_CLASSES; ++i) {
            if (classBits.get(i)) {
                classes.add(MarkingClasses.values()[i]);
            }
        }
        return classes;
    }

    @Override
    public boolean hasClass(MarkingClasses clazz) {
        return classBits.get(clazz.ordinal());
    }

    @Override
    public void setClass(MarkingClasses clazz) {
        classBits.set(clazz.ordinal());
    }

    @Override
    public void unsetClass(MarkingClasses clazz) {
        classBits.clear(clazz.ordinal());
    }

    @Override
    public Marking getWrappedMarking() {
        return this;
    }

}
