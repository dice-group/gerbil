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
package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.impl.nif.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests only the entity recognition and typing part of the task
 * without the entity linking.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class OKEChallengeTask1ETTest extends AbstractExperimentTaskTest {

    private static final String TEXTS[] = new String[] {
            "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
            "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
            "The senator received a Bachelor of Laws from the Columbia University." };
    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task1",
            "src/test/resources/OKE_Challenge/example_data/task1.ttl", false, ExperimentType.ETyping, null, null);
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/", "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#",
            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#");

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The extractor found everything
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(0, 20,
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding",
                                                new HashSet<String>(Arrays.asList(
                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(34, 6,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                        (Marking) new TypedNamedEntity(44, 6,
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney",
                                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                        "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location"))),
                        (Marking) new TypedNamedEntity(61, 21,
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(22, 14,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(57, 17,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                (Marking) new TypedNamedEntity(78, 12,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(96, 13,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(115, 16,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                (Marking) new TypedNamedEntity(184, 7,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
                new DocumentImpl(TEXTS[2], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(4, 7,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(49, 19,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The extractor made some mistakes at every second position
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                Arrays.asList((Marking) new TypedSpanImpl(0, 20,
                                        "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(44, 6,
                                        "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(61, 21, "http://www.w3.org/2002/07/owl#Thing"))),
                // P = 0, R = 0, F1 = 0
                // 2x correct P = 2/3, R = 2/3, F1=2/3
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedSpanImpl(22, 14, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(57, 17, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(78, 12, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(96, 13, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(115, 16, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"),
                                // P = 1.0, R = 1.0, F1 = 1.0
                                (Marking) new TypedSpanImpl(184, 7, "http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                // P = 1.0, R = 1.0, F1 = 1.0
                // 6xcorrect P=1.0,R=1.0,F=1.0
                new DocumentImpl(TEXTS[2], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                        Arrays.asList((Marking) new TypedSpanImpl(4, 7, "http://www.w3.org/2002/07/owl#Individual"),
                                // P = 0, R = 0, F1 = 0
                                (Marking) new TypedSpanImpl(49, 19, "http://www.w3.org/2002/07/owl#Individual"))) },
                GOLD_STD,
                // P = 0, R = 0, F1 = 0
                // 0x correct P = 0, R = 0, F = 0
                Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 5.0 / 9.0, 5.0 / 9.0, 5.0 / 9.0, 8.0 / 11.0, 8.0 / 11.0, 8.0 / 11.0, 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private Matching matching;

    public OKEChallengeTask1ETTest(Document[] annotatorResults, DatasetConfiguration dataset, Matching matching,
            double[] expectedResults) {
        this.annotatorResults = annotatorResults;
        this.dataset = dataset;
        this.expectedResults = expectedResults;
        this.matching = matching;
    }

    @Test
    public void test() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.ETyping), dataset,
                ExperimentType.ETyping);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
    }
}
