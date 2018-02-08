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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class HierarchicalMatchingsCounterTest {

    public static final String KNOWN_KB_URIS[] = new String[] { "http://example.org/" };

    /**
     * <p>
     * Some of the test cases have been taken from
     * "Evaluation Measures for Hierarchical Classification: a unified view and novel approaches"
     * by Kosmopoulos et al.
     * </p>
     * 
     * <p>
     * Test 1: Overspecialization (figure 2 a) (A is the highest node, C the
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
     * Test 2: Underspecialization (figure 2 b) (reusing the model from above)
     * <br>
     * gold standard = C <br>
     * annotator = B
     * </p>
     * <p>
     * Test 3: Overspecialization (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = C
     * </p>
     * <p>
     * Test 4: Underspecialization (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = A
     * </p>
     * <p>
     * Test 5: Exact Matching (reusing the model from above)<br>
     * gold standard = B <br>
     * annotator = B
     * </p>
     * <p>
     * Test 6: Exact Matching (reusing the model from above)<br>
     * gold standard = B,C <br>
     * annotator = B
     * </p>
     * <p>
     * Test 7: Exact Matching (reusing the model from above)<br>
     * gold standard = B <br>
     * annotator = B,C
     * </p>
     * <p>
     * Test 8: Alternative paths (figure 2 c)
     * 
     * <pre>
     *   A
     *  / \
     * B   C
     * |   |
     * |   D
     *  \ /
     *   E
     * </pre>
     * 
     * gold standard = E <br>
     * annotator = A
     * </p>
     * <p>
     * Test 9: Pairing problem (figure 2 d)
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
     * Test 10: Long distance problem (figure 2 d) (reusing the model from
     * above) <br>
     * gold standard = D <br>
     * annotator = F
     * </p>
     * <p>
     * Test 11: DAG example (figure 8 b)
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
     * Test 12: DAG example (reusing the model from above)<br>
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
         * Overspecialization (figure 2 a)
         */
        classModel = ModelFactory.createDefaultModel();
        resources = createResources(3, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[1]);
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI() },
                new String[] { resources[2].getURI() }, new int[] { 1, 0, 1 } });
        /*
         * Underspecialization (figure 2 b) reusing the model from above!
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[2].getURI() },
                new String[] { resources[1].getURI() }, new int[] { 1, 1, 0 } });
        /*
         * Overspecialization (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI(), resources[2].getURI() },
                new String[] { resources[2].getURI() }, new int[] { 1, 0, 1 } });
        /*
         * Underspecialization (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI(), resources[2].getURI() },
                new String[] { resources[0].getURI() }, new int[] { 2, 1, 0 } });
        /*
         * Exact matching (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI() },
                new String[] { resources[1].getURI() }, new int[] { 2, 0, 0 } });
        /*
         * Exact matching (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI(), resources[2].getURI() },
                new String[] { resources[1].getURI() }, new int[] { 2, 0, 0 } });
        /*
         * Exact matching (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI() },
                new String[] { resources[1].getURI(), resources[2].getURI() }, new int[] { 2, 0, 0 } });
        /*
         * Alternative paths (figure 2 c)
         */
        classModel = ModelFactory.createDefaultModel();
        resources = createResources(5, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[0]);
        classModel.add(resources[3], RDFS.subClassOf, resources[2]);
        classModel.add(resources[4], RDFS.subClassOf, resources[3]);
        classModel.add(resources[4], RDFS.subClassOf, resources[1]);
        testConfigs.add(new Object[] { classModel, new String[] { resources[4].getURI() },
                new String[] { resources[0].getURI() }, new int[] { 1, 4, 0 } });
        /*
         * Pairing problem (figure 2 d)
         */
        classModel = ModelFactory.createDefaultModel();
        resources = createResources(6, classModel);
        classModel.add(resources[1], RDFS.subClassOf, resources[0]);
        classModel.add(resources[2], RDFS.subClassOf, resources[0]);
        classModel.add(resources[3], RDFS.subClassOf, resources[1]);
        classModel.add(resources[4], RDFS.subClassOf, resources[1]);
        classModel.add(resources[5], RDFS.subClassOf, resources[2]);
        testConfigs.add(new Object[] { classModel, new String[] { resources[1].getURI(), resources[5].getURI() },
                new String[] { resources[3].getURI(), resources[4].getURI() }, new int[] { 2, 0, 2 } });
        /*
         * Long distance problem (figure 2 d) reusing the model from above!
         */
        testConfigs.add(new Object[] { classModel, new String[] { "http://example.org/D" },
                new String[] { "http://example.org/F" }, new int[] { 0, 1, 1 } });
        /*
         * DAG example (figure 8 b)
         */
        classModel = ModelFactory.createDefaultModel();
        resources = createResources(12, classModel);
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
        testConfigs.add(new Object[] { classModel,
                new String[] { resources[6].getURI(), resources[9].getURI(), resources[10].getURI() },
                new String[] { resources[7].getURI(), resources[10].getURI(), resources[11].getURI() },
                new int[] { 1, 2, 2 } });
        /*
         * DAG example (reusing the model from above)
         */
        testConfigs.add(new Object[] { classModel, new String[] { resources[3].getURI() },
                new String[] { resources[2].getURI() }, new int[] { 1, 1, 6 } });

        return testConfigs;
    }

    private Model typeHierarchy;
    private String goldStandardTypes[];
    private String annotatorResults[];
    private EvaluationCounts expectedCounts;

    public HierarchicalMatchingsCounterTest(Model typeHierarchy, String[] goldStandardTypes, String[] annotatorResults,
            int[] expectedCounts) {
        this.typeHierarchy = typeHierarchy;
        this.goldStandardTypes = goldStandardTypes;
        this.annotatorResults = annotatorResults;
        this.expectedCounts = new EvaluationCounts(expectedCounts[0], expectedCounts[1], expectedCounts[2]);
    }

    @Test
    public void test() {
        HierarchicalMatchingsCounter<TypedNamedEntity> counter = new HierarchicalMatchingsCounter<TypedNamedEntity>(
                new WeakSpanMatchingsSearcher<TypedNamedEntity>(),
                new SimpleWhiteListBasedUriKBClassifier(KNOWN_KB_URIS),
                SimpleSubClassInferencerFactory.createInferencer(typeHierarchy));

        List<TypedNamedEntity> annotatorResult = new ArrayList<TypedNamedEntity>();
        annotatorResult.add(createTypedNamedEntities(annotatorResults, 0));
        List<TypedNamedEntity> goldStandard = new ArrayList<TypedNamedEntity>();
        goldStandard.add(createTypedNamedEntities(goldStandardTypes, 0));
        List<EvaluationCounts> evalCounts = counter.countMatchings(annotatorResult, goldStandard);

        Assert.assertNotNull(evalCounts);
        Assert.assertTrue(evalCounts.size() > 0);
        Assert.assertEquals("Arrays do not equal exp=" + expectedCounts + " calculated=" + evalCounts.get(0),
                expectedCounts, evalCounts.get(0));
    }

    public static TypedNamedEntity createTypedNamedEntities(String types[], int id) {
        return new TypedNamedEntity(id * 2, (id * 2) + 1, KNOWN_KB_URIS[0] + "entity_" + id,
                new HashSet<String>(Arrays.asList(types)));
    }

    public static Resource[] createResources(int numberOfResources, Model classModel) {
        Resource resources[] = new Resource[numberOfResources];
        int startChar = (int) 'A';
        for (int i = 0; i < resources.length; ++i) {
            resources[i] = classModel.createResource(KNOWN_KB_URIS[0] + ((char) (startChar + i)));
            classModel.add(resources[i], RDF.type, RDFS.Class);
        }
        return resources;
    }
}
