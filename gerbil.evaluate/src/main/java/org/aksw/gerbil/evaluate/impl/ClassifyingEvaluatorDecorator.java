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
package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.ClassifiedMarkingFactory;
import org.aksw.gerbil.evaluate.AbstractTypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.TypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.matching.impl.clas.MarkingClassifier;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * This {@link TypeTransformingEvaluatorDecorator} transforms {@link Marking}
 * instances into {@link ClassifiedMarking} instances based on the given
 * {@link MarkingClassifier} instances.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <U>
 *            The {@link Marking} class
 * @param <V>
 *            The {@link ClassifiedMarking} class
 */
public class ClassifyingEvaluatorDecorator<U extends Marking, V extends ClassifiedMarking>
        extends AbstractTypeTransformingEvaluatorDecorator<U, V> {

    protected MarkingClassifier<V> classifiers[];

    public ClassifyingEvaluatorDecorator(Evaluator<V> evaluator,
            @SuppressWarnings("unchecked") MarkingClassifier<V>... classifiers) {
        super(evaluator);
        this.classifiers = classifiers;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<V> changeType(List<U> markings) {
        List<V> classifiedMarkings = new ArrayList<V>(markings.size());
        V classifiedMarking;
        for (U marking : markings) {
            classifiedMarking = (V) ClassifiedMarkingFactory.createClassifiedMeaning(marking);
            for (int i = 0; i < classifiers.length; ++i) {
                classifiers[i].classify(classifiedMarking);
            }
            classifiedMarkings.add(classifiedMarking);
        }
        return classifiedMarkings;
    }

}
