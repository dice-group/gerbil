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
package org.aksw.gerbil.matching.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencerFactory;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 * This JUnit test is very similar to {@link HierarchicalMatchingsCounterTest},
 * but sends several different cases to the matchings counter before comparing
 * results which is more similar to the normal usage and should make sure, that
 * the counter can count correctly more than only once.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class HierarchicalMatchingsCounterTest2 {

    private static final String KNOWN_KB_URIS[] = new String[] { "http://example.org/" };

    /**
     * <p>
     * Some of the test cases have been taken from
     * "Evaluation Measures for Hierarchical Classification: a unified view and novel approaches"
     * by Kosmopoulos et al.
     * </p>
     * 
     * <p>
     * Test 1.1: Overspecialization (figure 2 a) (A is the highest node, C the
     * lowest)
     * 
     * <pre>
     * A - B - C
     * </pre>
     * 
     * gold standard = B <br>
     * annotator = C
     * </p>
     * <p>
     * Test 1.2: Underspecialization (figure 2 b) (reusing the model from above)
     * <br>
     * gold standard = C <br>
     * annotator = B
     * </p>
     * <p>
     * Test 1.3: Overspecialization (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = C
     * </p>
     * <p>
     * Test 1.4: Underspecialization (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = A
     * </p>
     * <p>
     * Test 1.5: Exact Matching (reusing the model from above)<br>
     * gold standard = B <br>
     * annotator = B
     * </p>
     * <p>
     * Test 1.6: Exact Matching (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = B
     * </p>
     * <p>
     * Test 1.7: Exact Matching (reusing the model from above)<br>
     * gold standard = B <br>
     * annotator = B,C
     * </p>
     * <p>
     * Test 2.1: Pairing problem (figure 2 d)
     * 
     * <pre>
     *     A
     *    / \
     *   B   C
     *  / \   \
     * D   E   F
     * </pre>
     * 
     * gold standard = B, F <br>
     * annotator = D, E
     * </p>
     * <p>
     * Test 2.2: Long distance problem (figure 2 d) (reusing the model from
     * above) <br>
     * gold standard = D <br>
     * annotator = F
     * </p>
     * <p>
     * Test 3.1: DAG example (figure 8 b)
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
     * gold standard = G, J, K <br>
     * annotator = H, K, L
     * </p>
     * <p>
     * Test 3.2: DAG example (reusing the model from above)<br>
     * gold standard = D <br>
     * annotator = C
     * </p>
     * 
     * @return
     */
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        Model classModel;

        Resource resources[];
        /*
         * test 1
         */
        classModel = ModelFactory.createDefaultModel();
        resources = HierarchicalMatchingsCounterTest.createResources(3, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[1]);
        testConfigs.add(new Object[] { classModel,
                new Object[][] {
                        new Object[] { new String[] { resources[1].getURI() }, new String[] { resources[2].getURI() },
                                new int[] { 1, 0, 1 } },
                        new Object[] { new String[] { resources[2].getURI() }, new String[] { resources[1].getURI() },
                                new int[] { 1, 1, 0 } },
                new Object[] { new String[] { resources[1].getURI(), resources[2].getURI() },
                        new String[] { resources[2].getURI() }, new int[] { 1, 0, 1 } },
                new Object[] { new String[] { resources[1].getURI(), resources[2].getURI() },
                        new String[] { resources[0].getURI() }, new int[] { 2, 1, 0 } },
                new Object[] { new String[] { resources[1].getURI() }, new String[] { resources[1].getURI() },
                        new int[] { 2, 0, 0 } },
                new Object[] { new String[] { resources[1].getURI(), resources[2].getURI() },
                        new String[] { resources[1].getURI() }, new int[] { 2, 0, 0 } },
                new Object[] { new String[] { resources[1].getURI() },
                        new String[] { resources[1].getURI(), resources[2].getURI() }, new int[] { 2, 0, 0 } } } });
        /*
         * test 2
         */
        classModel = ModelFactory.createDefaultModel();
        resources = HierarchicalMatchingsCounterTest.createResources(6, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[0]);
        classModel.add(resources[3], RDFS.subClassOf, resources[1]);
        classModel.add(resources[4], RDFS.subClassOf, resources[1]);
        classModel.add(resources[5], RDFS.subClassOf, resources[2]);
        testConfigs.add(new Object[] { classModel,
                new Object[][] {
                        new Object[] { new String[] { resources[1].getURI(), resources[5].getURI() },
                                new String[] { resources[3].getURI(), resources[4].getURI() }, new int[] { 2, 0, 2 } },
                        new Object[] { new String[] { "http://example.org/D" }, new String[] { "http://example.org/F" },
                                new int[] { 0, 1, 1 } } } });
        /*
         * test 3
         */
        classModel = ModelFactory.createDefaultModel();
        resources = HierarchicalMatchingsCounterTest.createResources(12, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[0]);
        classModel.add(resources[3], RDFS.subClassOf, resources[0]);
        classModel.add(resources[4], RDFS.subClassOf, resources[1]);
        classModel.add(resources[5], RDFS.subClassOf, resources[1]);
        classModel.add(resources[6], RDFS.subClassOf, resources[2]);
        classModel.add(resources[6], RDFS.subClassOf, resources[3]);
        classModel.add(resources[7], RDFS.subClassOf, resources[3]);
        classModel.add(resources[8], RDFS.subClassOf, resources[3]);
        classModel.add(resources[9], RDFS.subClassOf, resources[3]);
        classModel.add(resources[10], RDFS.subClassOf, resources[8]);
        classModel.add(resources[11], RDFS.subClassOf, resources[3]);
        classModel.add(resources[11], RDFS.subClassOf, resources[8]);
        testConfigs.add(new Object[] { classModel, new Object[][] {
                new Object[] { new String[] { resources[6].getURI(), resources[9].getURI(), resources[10].getURI() },
                        new String[] { resources[7].getURI(), resources[10].getURI(), resources[11].getURI() },
                        new int[] { 1, 2, 2 } },
                new Object[] { new String[] { resources[3].getURI() }, new String[] { resources[2].getURI() },
                        new int[] { 1, 1, 6 } } } });

        return testConfigs;
    }

    private Model typeHierarchy;
    private Object testCases[][];

    public HierarchicalMatchingsCounterTest2(Model typeHierarchy, Object testCases[][]) {
        this.typeHierarchy = typeHierarchy;
        this.testCases = testCases;
    }

    @Test
    public void test() {
        HierarchicalMatchingsCounter<TypedNamedEntity> counter = new HierarchicalMatchingsCounter<TypedNamedEntity>(
                new WeakSpanMatchingsSearcher<TypedNamedEntity>(),
                new SimpleWhiteListBasedUriKBClassifier(KNOWN_KB_URIS),
                SimpleSubClassInferencerFactory.createInferencer(typeHierarchy));

        List<TypedNamedEntity> annotatorResult, goldStandard;
        List<List<EvaluationCounts>> counts = new ArrayList<List<EvaluationCounts>>();
        for (int i = 0; i < testCases.length; ++i) {
            annotatorResult = new ArrayList<TypedNamedEntity>();
            annotatorResult
                    .add(HierarchicalMatchingsCounterTest.createTypedNamedEntities((String[]) testCases[i][1], 0));
            goldStandard = new ArrayList<TypedNamedEntity>();
            goldStandard.add(HierarchicalMatchingsCounterTest.createTypedNamedEntities((String[]) testCases[i][0], 0));
            counts.add(counter.countMatchings(annotatorResult, goldStandard));
        }

        Assert.assertNotNull(counts);
        Assert.assertEquals(testCases.length, counts.size());
        int expectedArray[];
        EvaluationCounts expectedCounts;
        for (int i = 0; i < counts.size(); ++i) {
            Assert.assertNotNull(counts.get(i));
            Assert.assertTrue(counts.get(i).size() > 0);
            expectedArray = (int[]) testCases[i][2];
            expectedCounts = new EvaluationCounts(expectedArray[0], expectedArray[1], expectedArray[2]);
            Assert.assertEquals("Arrays do not equal exp=" + expectedCounts + " calculated=" + counts.get(i).get(0),
                    expectedCounts, counts.get(i).get(0));
        }
    }

}
