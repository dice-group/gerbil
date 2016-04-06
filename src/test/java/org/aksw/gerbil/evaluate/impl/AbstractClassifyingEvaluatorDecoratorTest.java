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

import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.junit.Assert;

public abstract class AbstractClassifyingEvaluatorDecoratorTest<T extends ClassifiedMarking> implements Evaluator<T> {

    protected int expectedGSClassification[];
    protected int expectedAnnoClassification[];
    protected MarkingClasses clazz;

    public AbstractClassifyingEvaluatorDecoratorTest(int[] expectedGSClassification, int[] expectedAnnoClassification,
            MarkingClasses clazz) {
        this.expectedGSClassification = expectedGSClassification;
        this.expectedAnnoClassification = expectedAnnoClassification;
        this.clazz = clazz;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        checkClassification(annotatorResults.get(0), expectedAnnoClassification, clazz);
        checkClassification(goldStandard.get(0), expectedGSClassification, clazz);
    }

    private void checkClassification(List<T> classifiedMarkings, int[] expectedClassification, MarkingClasses clazz) {
        Assert.assertEquals(expectedClassification.length, classifiedMarkings.size());
        T marking;
        for (int i = 0; i < expectedClassification.length; ++i) {
            marking = classifiedMarkings.get(i);
            if (expectedClassification[i] > 0) {
                Assert.assertTrue(
                        "Expected the marking \"" + marking.toString() + "\" to have the class " + clazz + ".",
                        marking.hasClass(clazz));
            } else {
                Assert.assertFalse(
                        "Did not expected the marking \"" + marking.toString() + "\" to have the class " + clazz + ".",
                        marking.hasClass(clazz));
            }
        }
    }
}
