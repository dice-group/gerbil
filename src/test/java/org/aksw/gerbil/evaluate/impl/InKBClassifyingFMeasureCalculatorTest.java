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

import org.aksw.gerbil.datatypes.marking.ClassifiedMeaning;
import org.aksw.gerbil.datatypes.marking.MarkingClasses;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.impl.AbstractMeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.ClassifiedMeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.ClassifierBasedMeaningMatchingsSearcher;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.matching.impl.clas.EmergingEntityMeaningClassifier;
import org.aksw.gerbil.matching.impl.clas.UriBasedMeaningClassifier;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class InKBClassifyingFMeasureCalculatorTest {

    private static final double DELTA = 0.000001;
    private static final UriKBClassifier CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier("http://kb/");
    @SuppressWarnings("unused")
    private static final AbstractMeaningMatchingsSearcher<Meaning> SEARCHER = new ClassifierBasedMeaningMatchingsSearcher<Meaning>(
            CLASSIFIER);

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // empty test case (tp=0,fn=0,fp=0)
        testConfigs.add(new Object[] { new Meaning[] {}, new Meaning[] {}, new double[] { 1, 1, 1 }, null, null });
        // test case with empty annotator results (tp=0,fn=1,fp=0)
        testConfigs.add(new Object[] { new Meaning[] { new Annotation("http://kb/1") }, new Meaning[] {},
                new double[] { 0, 0, 0 }, new double[] { 0, 0, 0 }, null });
        // test case with empty gold standard (tp=0,fn=0,fp=1)
        testConfigs.add(new Object[] { new Meaning[] {}, new Meaning[] { new Annotation("http://kb/1") },
                new double[] { 0, 0, 0 }, new double[] { 0, 0, 0 }, null });
        // test case with single exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] { new Meaning[] { new Annotation("http://kb/1") },
                new Meaning[] { new Annotation("http://kb/1") }, new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 },
                null });
        // test case with empty annotator results (tp=0,fn=1,fp=0)
        testConfigs.add(new Object[] { new Meaning[] { new Annotation("http://ukb/1") }, new Meaning[] {},
                new double[] { 0, 0, 0 }, null, new double[] { 0, 0, 0 } });
        // test case with empty gold standard (tp=0,fn=0,fp=1)
        testConfigs.add(new Object[] { new Meaning[] {}, new Meaning[] { new Annotation("http://ukb/1") },
                new double[] { 0, 0, 0 }, null, new double[] { 0, 0, 0 } });
        // test case with single exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] { new Meaning[] { new Annotation("http://ukb/1") },
                new Meaning[] { new Annotation("http://ukb/1") }, new double[] { 1, 1, 1 }, null,
                new double[] { 1, 1, 1 } });
        // test case with several exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] {
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                        new Annotation("http://kb/3") },
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                        new Annotation("http://kb/3") },
                new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, null });
        // test case with several exact matching Meanings (tp=1,fn=0,fp=0)
        testConfigs.add(new Object[] {
                new Meaning[] { new Annotation("http://ukb/1"), new Annotation("http://ukb/2"),
                        new Annotation("http://ukb/3") },
                new Meaning[] { new Annotation("http://ukb/1"), new Annotation("http://ukb/2"),
                        new Annotation("http://ukb/3") },
                new double[] { 1, 1, 1 }, null, new double[] { 1, 1, 1 } });
        // test case with several exact matching Meanings with a different
        // order
        testConfigs.add(new Object[] {
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                        new Annotation("http://kb/3") },
                new Meaning[] { new Annotation("http://kb/2"), new Annotation("http://kb/3"),
                        new Annotation("http://kb/1") },
                new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, null });
        // test case with several exact matching Meanings with the same URIs
        testConfigs.add(new Object[] {
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/1"),
                        new Annotation("http://kb/1") },
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/1"),
                        new Annotation("http://kb/1") },
                new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, null });
        // test case with several exact matching Meanings with two of them
        // that couldn't be mapped to the KB
        testConfigs.add(new Object[] {
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://ukb/2"),
                        new Annotation("http://ukb/3") },
                new Meaning[] { new Annotation("http://aukb/2"), new Annotation("http://aukb/3"),
                        new Annotation("http://kb/1") },
                new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 } });
        // test case with one exact matching Meanings, one wrong matching
        // and a missing matching
        testConfigs.add(new Object[] { new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://ukb/2") },
                new Meaning[] { new Annotation("http://kb/1"), new Annotation("http://kb/2"),
                        new Annotation("http://kb/3") },
                new double[] { 1.0 / 3.0, 1.0 / 2.0, 2.0 / 5.0 }, new double[] { 1.0 / 3.0, 1.0, 0.5 },
                new double[] { 0, 0, 0 } });
        return testConfigs;
    }

    private Meaning goldStandard[];
    private Meaning annotatorResponse[];
    private double expectedResults[];
    private double expectedInKbResults[];
    private double expectedEEResults[];

    public InKBClassifyingFMeasureCalculatorTest(Meaning[] goldStandard, Meaning[] annotatorResponse,
            double[] expectedResults, double[] expectedInKbResults, double[] expectedEEResults) {
        this.goldStandard = goldStandard;
        this.annotatorResponse = annotatorResponse;
        this.expectedResults = expectedResults;
        this.expectedInKbResults = expectedInKbResults;
        this.expectedEEResults = expectedEEResults;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Test
    public void test() {
        Evaluator<Meaning> evaluator = new ClassifyingEvaluatorDecorator<Meaning, ClassifiedMeaning>(
                new ClassConsideringFMeasureCalculator<ClassifiedMeaning>(
                        new MatchingsCounterImpl<ClassifiedMeaning>(
                                new ClassifiedMeaningMatchingsSearcher<ClassifiedMeaning>()),
                        MarkingClasses.IN_KB, MarkingClasses.EE),
                new UriBasedMeaningClassifier<ClassifiedMeaning>(CLASSIFIER, MarkingClasses.IN_KB),
                new EmergingEntityMeaningClassifier<ClassifiedMeaning>());
        EvaluationResultContainer results = new EvaluationResultContainer();
        evaluator.evaluate(Arrays.asList(Arrays.asList(annotatorResponse)), Arrays.asList(Arrays.asList(goldStandard)),
                results);
        double expectedValue, resultValue;
        boolean containedInKB = false;
        boolean containedEE = false;
        for (EvaluationResult result : results.getResults()) {
            switch (result.getName()) {
            case FMeasureCalculator.MACRO_PRECISION_NAME:
            case FMeasureCalculator.MICRO_PRECISION_NAME: {
                expectedValue = expectedResults[0];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_RECALL_NAME:
            case FMeasureCalculator.MICRO_RECALL_NAME: {
                expectedValue = expectedResults[1];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_F1_SCORE_NAME:
            case FMeasureCalculator.MICRO_F1_SCORE_NAME: {
                expectedValue = expectedResults[2];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.IN_KB_MACRO_PRECISION_NAME:
            case InKBClassBasedFMeasureCalculator.IN_KB_MICRO_PRECISION_NAME: {
                containedInKB = true;
                Assert.assertNotNull(expectedInKbResults);
                expectedValue = expectedInKbResults[0];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.IN_KB_MACRO_RECALL_NAME:
            case InKBClassBasedFMeasureCalculator.IN_KB_MICRO_RECALL_NAME: {
                containedInKB = true;
                Assert.assertNotNull(expectedInKbResults);
                expectedValue = expectedInKbResults[1];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.IN_KB_MACRO_F1_SCORE_NAME:
            case InKBClassBasedFMeasureCalculator.IN_KB_MICRO_F1_SCORE_NAME: {
                containedInKB = true;
                Assert.assertNotNull(expectedInKbResults);
                expectedValue = expectedInKbResults[2];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.EE_MACRO_PRECISION_NAME:
            case InKBClassBasedFMeasureCalculator.EE_MICRO_PRECISION_NAME: {
                containedEE = true;
                Assert.assertNotNull(expectedEEResults);
                expectedValue = expectedEEResults[0];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.EE_MACRO_RECALL_NAME:
            case InKBClassBasedFMeasureCalculator.EE_MICRO_RECALL_NAME: {
                containedEE = true;
                Assert.assertNotNull(expectedEEResults);
                expectedValue = expectedEEResults[1];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case InKBClassBasedFMeasureCalculator.EE_MACRO_F1_SCORE_NAME:
            case InKBClassBasedFMeasureCalculator.EE_MICRO_F1_SCORE_NAME: {
                containedEE = true;
                Assert.assertNotNull(expectedEEResults);
                expectedValue = expectedEEResults[2];
                resultValue = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            default: {
                resultValue = expectedValue = -1;
            }
            }
            if (expectedValue != -1) {
                Assert.assertEquals("wrong result for " + result.getName(), expectedValue, resultValue, DELTA);
            }
        }
        if (!containedInKB) {
            Assert.assertNull(expectedInKbResults);
        }
        if (!containedEE) {
            Assert.assertNull(expectedEEResults);
        }
    }
}
