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
package org.aksw.gerbil.matching.impl.clas;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This classifier is used to set the {@link MarkingClasses#EE} flag of
 * {@link ClassifiedMarking}s. Since it simply inverts the
 * {@link MarkingClasses#IN_KB} flag, it is required to use a classifier
 * that sets classifies the {@link Marking} regarding this class before this
 * classifier is used.
 */
public class EmergingEntityMeaningClassifier<T extends ClassifiedMarking> implements MarkingClassifier<T> {

    @Override
    public void classify(T marking) {
        if (!marking.hasClass(MarkingClasses.IN_KB)) {
            marking.setClass(MarkingClasses.EE);
        }
    }

}