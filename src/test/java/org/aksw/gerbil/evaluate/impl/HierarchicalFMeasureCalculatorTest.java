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
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.MatchingsSearcher;
import org.aksw.gerbil.matching.MatchingsSearcherFactory;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounter;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounterTest;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencerFactory;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class HierarchicalFMeasureCalculatorTest {

    private static final Model CLASS_MODEL = ModelFactory.createDefaultModel();
    private static final Resource RESOURCES[] = HierarchicalMatchingsCounterTest.createResources(12, CLASS_MODEL);
    private static final SubClassInferencer inferencer = createSubClassInferencer();

    public static SubClassInferencer createSubClassInferencer() {
        /**
         * Creates the model:
         * 
         * <pre>
         *       A
         *     / | \
         *   B   C   D
         *  / \  |/´/|\`\
         * E   F G H I \ J
         *          / \|
         *         K   L
         * </pre>
         * 
         */
        CLASS_MODEL.add(RESOURCES[1], RDFS.subClassOf, RESOURCES[0]);
        CLASS_MODEL.add(RESOURCES[2], RDFS.subClassOf, RESOURCES[0]);
        CLASS_MODEL.add(RESOURCES[3], RDFS.subClassOf, RESOURCES[0]);
        CLASS_MODEL.add(RESOURCES[4], RDFS.subClassOf, RESOURCES[1]);
        CLASS_MODEL.add(RESOURCES[5], RDFS.subClassOf, RESOURCES[1]);
        CLASS_MODEL.add(RESOURCES[6], RDFS.subClassOf, RESOURCES[2]);
        CLASS_MODEL.add(RESOURCES[6], RDFS.subClassOf, RESOURCES[3]);
        CLASS_MODEL.add(RESOURCES[7], RDFS.subClassOf, RESOURCES[3]);
        CLASS_MODEL.add(RESOURCES[8], RDFS.subClassOf, RESOURCES[3]);
        CLASS_MODEL.add(RESOURCES[9], RDFS.subClassOf, RESOURCES[3]);
        CLASS_MODEL.add(RESOURCES[10], RDFS.subClassOf, RESOURCES[8]);
        CLASS_MODEL.add(RESOURCES[11], RDFS.subClassOf, RESOURCES[3]);
        CLASS_MODEL.add(RESOURCES[11], RDFS.subClassOf, RESOURCES[8]);
        return SimpleSubClassInferencerFactory.createInferencer(CLASS_MODEL);
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // gold standard = G, J, K
        // annotator = H, K, L
        // tp = 1, fp = 2, fn = 2
        testConfigs.add(new Object[] {
                new String[][][] { new String[][] {
                        new String[] { RESOURCES[6].getURI(), RESOURCES[9].getURI(), RESOURCES[10].getURI() },
                        new String[] { RESOURCES[7].getURI(), RESOURCES[10].getURI(), RESOURCES[11].getURI() } } },
                new double[] { 1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0 } });
        // gold standard = D
        // annotator = C
        // tp = 1, fp = 1, fn = 6
        testConfigs.add(new Object[] {
                new String[][][] { new String[][] { new String[] { RESOURCES[3].getURI() },
                        new String[] { RESOURCES[2].getURI() } } },
                new double[] { 1.0 / 2.0, 1.0 / 7.0, 2.0 / 9.0, 1.0 / 2.0, 1.0 / 7.0, 2.0 / 9.0 } });
        // Both of the cases above
        testConfigs
                .add(new Object[] {
                        new String[][][] {
                                new String[][] {
                                        new String[] { RESOURCES[6].getURI(), RESOURCES[9].getURI(),
                                                RESOURCES[10].getURI() },
                                        new String[] { RESOURCES[7].getURI(), RESOURCES[10].getURI(),
                                                RESOURCES[11].getURI() } },
                                new String[][] { new String[] { RESOURCES[3].getURI() },
                                        new String[] { RESOURCES[2].getURI() } } },
                        new double[] { 5.0 / 12.0, 5.0 / 21.0, 10.0 / 33.0, 5.0 / 12.0, 5.0 / 21.0, 5.0 / 18.0 } });
        return testConfigs;
    }

    private String goldStandardTypes[][];
    private String annotatorResults[][];
    private double[] expectedResults;

    public HierarchicalFMeasureCalculatorTest(String[][][] cases, double[] expectedResults) {
        this.expectedResults = expectedResults;
        goldStandardTypes = new String[cases.length][];
        annotatorResults = new String[cases.length][];
        for (int i = 0; i < cases.length; ++i) {
            goldStandardTypes[i] = cases[i][0];
            annotatorResults[i] = cases[i][1];
        }
    }

    @Test
    public void test() {
        @SuppressWarnings("unchecked")
        HierarchicalFMeasureCalculator<TypedSpan> calculator = new HierarchicalFMeasureCalculator<TypedSpan>(
                new HierarchicalMatchingsCounter<TypedSpan>((MatchingsSearcher<TypedSpan>) MatchingsSearcherFactory
                        .createSpanMatchingsSearcher(Matching.WEAK_ANNOTATION_MATCH),
                        new SimpleWhiteListBasedUriKBClassifier(HierarchicalMatchingsCounterTest.KNOWN_KB_URIS),
                        inferencer));

        List<List<TypedSpan>> annotatorResult = new ArrayList<List<TypedSpan>>();
        List<List<TypedSpan>> goldStandard = new ArrayList<List<TypedSpan>>();
        List<TypedSpan> tempList;
        for (int i = 0; i < annotatorResults.length; ++i) {
            tempList = new ArrayList<TypedSpan>();
            tempList.add(createTypedNamedEntities(annotatorResults[i], i));
            annotatorResult.add(tempList);
            tempList = new ArrayList<TypedSpan>();
            tempList.add(createTypedNamedEntities(goldStandardTypes[i], i));
            goldStandard.add(tempList);
        }
        EvaluationResultContainer results = new EvaluationResultContainer();
        calculator.evaluate(annotatorResult, goldStandard, results);

        List<EvaluationResult> singleResults = results.getResults();
        Assert.assertEquals(expectedResults.length, singleResults.size());
        double calculatedResult[] = new double[6];
        for (EvaluationResult result : singleResults) {
            switch (result.getName()) {
            case FMeasureCalculator.MACRO_F1_SCORE_NAME: {
                calculatedResult[5] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_PRECISION_NAME: {
                calculatedResult[3] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_RECALL_NAME: {
                calculatedResult[4] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_F1_SCORE_NAME: {
                calculatedResult[2] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_PRECISION_NAME: {
                calculatedResult[0] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_RECALL_NAME: {
                calculatedResult[1] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            default: {
                throw new IllegalStateException("Got an unexpected result: " + result.getName());
            }
            }
        }
        Assert.assertArrayEquals("Arrays do not equal exp=" + Arrays.toString(expectedResults) + " calculated="
                + Arrays.toString(calculatedResult), expectedResults, calculatedResult, 0.000000001);
    }

    public static TypedSpan createTypedNamedEntities(String types[], int id) {
        return new TypedSpanImpl(id * 2, (id * 2) + 1, new HashSet<String>(Arrays.asList(types)));
    }
}
