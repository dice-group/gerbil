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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedSpanMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.impl.clas.EmergingEntityMeaningClassifier;
import org.aksw.gerbil.matching.impl.clas.UriBasedMeaningClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EEClassifyingEvaluatorDecoratorTest
        extends AbstractClassifyingEvaluatorDecoratorTest<ClassifiedSpanMeaning> {

    private static final UriKBClassifier CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier("http://kb/");

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // empty test case (tp=0,fn=0,fp=0)
        testConfigs.add(new Object[] { new MeaningSpan[] {}, new MeaningSpan[] {}, new int[] {}, new int[] {} });
        // test case with empty annotator results (tp=0,fn=1,fp=0)
        testConfigs.add(new Object[] { new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1") }, new MeaningSpan[] {},
                new int[] { 0 }, new int[] {} });
        // test case with empty gold standard (tp=0,fn=0,fp=1)
        testConfigs.add(new Object[] { new MeaningSpan[] {}, new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1") },
                new int[] {}, new int[] { 0 } });
        // test case with single exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] { new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1") },
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1") }, new int[] { 0 }, new int[] { 0 } });
        // both lists contain the same meaning but with different positions
        testConfigs.add(new Object[] { new MeaningSpan[] { new NamedEntity(10, 5, "http://kb/1") },
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1") }, new int[] { 0 }, new int[] { 0 } });
        // test case with empty annotator results (tp=0,fn=1,fp=0)
        testConfigs.add(new Object[] { new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1") },
                new MeaningSpan[] {}, new int[] { 1 }, new int[] {} });
        // test case with empty gold standard (tp=0,fn=0,fp=1)
        testConfigs.add(new Object[] { new MeaningSpan[] {},
                new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1") }, new int[] {}, new int[] { 1 } });
        // test case with single exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] { new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1") },
                new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1") }, new int[] { 1 }, new int[] { 1 } });
        // test case with several exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] {
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"), new NamedEntity(10, 5, "http://kb/2"),
                        new NamedEntity(20, 5, "http://kb/3") },
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"), new NamedEntity(10, 5, "http://kb/2"),
                        new NamedEntity(20, 5, "http://kb/3") },
                new int[] { 0, 0, 0 }, new int[] { 0, 0, 0 } });
        // test case with several exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] {
                new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1"), new NamedEntity(10, 5, "http://ukb/2"),
                        new NamedEntity(20, 5, "http://ukb/3") },
                new MeaningSpan[] { new NamedEntity(0, 5, "http://ukb/1"), new NamedEntity(10, 5, "http://ukb/2"),
                        new NamedEntity(20, 5, "http://ukb/3") },
                new int[] { 1, 1, 1 }, new int[] { 1, 1, 1 } });
        // test case with several exact matching Meanings with a different
        // order
        testConfigs.add(new Object[] {
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"), new NamedEntity(10, 5, "http://kb/2"),
                        new NamedEntity(20, 5, "http://kb/3") },
                new MeaningSpan[] { new NamedEntity(10, 5, "http://kb/2"), new NamedEntity(20, 5, "http://kb/3"),
                        new NamedEntity(0, 5, "http://kb/1") },
                new int[] { 0, 0, 0 }, new int[] { 0, 0, 0 } });
        // test case with several exact matching Meanings with two of them
        // that couldn't be mapped to the KB
        testConfigs.add(new Object[] {
                new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"), new NamedEntity(10, 5, "http://ukb/2"),
                        new NamedEntity(20, 5, "http://ukb/3") },
                new MeaningSpan[] { new NamedEntity(10, 5, "http://aukb/2"), new NamedEntity(20, 5, "http://aukb/3"),
                        new NamedEntity(0, 5, "http://kb/1") },
                new int[] { 0, 1, 1 }, new int[] { 1, 1, 0 } });
        // test case with one exact matching Meanings, one wrong matching
        // and a missing matching
        testConfigs
                .add(new Object[] {
                        new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"),
                                new NamedEntity(10, 5, "http://ukb/2") },
                        new MeaningSpan[] { new NamedEntity(0, 5, "http://kb/1"), new NamedEntity(10, 5, "http://kb/2"),
                                new NamedEntity(20, 5, "http://kb/3") },
                        new int[] { 0, 1 }, new int[] { 0, 0, 0 } });
        return testConfigs;
    }

    private MeaningSpan goldStandard[];
    private MeaningSpan annotatorResponse[];

    public EEClassifyingEvaluatorDecoratorTest(MeaningSpan[] goldStandard, MeaningSpan[] annotatorResponse,
            int[] expectedGSClassification, int[] expectedAnnoClassification) {
        super(expectedGSClassification, expectedAnnoClassification, MarkingClasses.EE);
        this.goldStandard = goldStandard;
        this.annotatorResponse = annotatorResponse;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Evaluator<MeaningSpan> evaluator = new ClassifyingEvaluatorDecorator<MeaningSpan, ClassifiedSpanMeaning>(this,
                new UriBasedMeaningClassifier<ClassifiedSpanMeaning>(CLASSIFIER, MarkingClasses.IN_KB),
                new EmergingEntityMeaningClassifier<ClassifiedSpanMeaning>());
        evaluator.evaluate(Arrays.asList(Arrays.asList(annotatorResponse)), Arrays.asList(Arrays.asList(goldStandard)),
                null);
    }
}
