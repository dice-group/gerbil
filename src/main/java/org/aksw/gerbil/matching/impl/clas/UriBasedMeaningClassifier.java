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

import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;

public class UriBasedMeaningClassifier<T extends ClassifiedMeaning> implements MarkingClassifier<T> {

    protected UriKBClassifier classifier;
    protected MarkingClasses clazz;

    public UriBasedMeaningClassifier(UriKBClassifier classifier, MarkingClasses clazz) {
        this.classifier = classifier;
        this.clazz = clazz;
    }

    @Override
    public void classify(T marking) {
        if (classifier.containsKBUri(marking.getUris())) {
            marking.setClass(clazz);
        }
    }
}
