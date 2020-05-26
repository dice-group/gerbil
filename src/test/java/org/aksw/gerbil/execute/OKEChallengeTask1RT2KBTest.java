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
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.impl.nif.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OKEChallengeTask1RT2KBTest extends AbstractExperimentTaskTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    private static final String TEXTS[] = new String[] {
            "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
            "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
            "The senator received a Bachelor of Laws from the Columbia University." };
    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task1",
            "src/test/resources/OKE_Challenge/example_data/task1.ttl", false, ExperimentType.RT2KB, null, null);
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The extractor found everything and marked all entities using dbpedia
        // URIs (if they were available)
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-1",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(0, 20,
                                                "http://dbpedia.org/resource/Florence_May_Harding",
                                                new HashSet<String>(Arrays.asList(
                                                        "http://www.w3.org/2002/07/owl#Individual",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(44, 6, "http://dbpedia.org/resource/Sydney",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location"))),
                        (Marking) new TypedNamedEntity(61, 21,
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(22, 14, "http://dbpedia.org/resource/James_Carville",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(57, 17,
                                        "http://dbpedia.org/resource/Political_consulting",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                (Marking) new TypedNamedEntity(78, 12, "http://dbpedia.org/resource/Bill_Clinton",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(96, 13, "http://dbpedia.org/resource/Donna_Brazile",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(115, 16, "http://dbpedia.org/resource/Campaign_manager",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Role"))),
                                (Marking) new TypedNamedEntity(184, 7, "http://dbpedia.org/resource/Al_Gore",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))))),
                new DocumentImpl(TEXTS[2], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/sentence-3",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(4, 7,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                (Marking) new TypedNamedEntity(49, 19,
                                        "http://dbpedia.org/resource/Columbia_University",
                                        new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private Matching matching;

    public OKEChallengeTask1RT2KBTest(Document[] annotatorResults, DatasetConfiguration dataset, Matching matching,
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
                new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.RT2KB), dataset,
                ExperimentType.RT2KB);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
    }
}
