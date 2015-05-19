package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.annotator.TestOKETask2Annotator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class OKEChallengeTask2Test extends AbstractExperimentTaskTest {

    private static final String TEXTS[] = new String[] {
            "Brian Banner is a fictional villain from the Marvel Comics Universe created by Bill Mantlo and Mike Mignola and first appearing in print in late 1985.",
            "Avex Group Holdings Inc., listed in the Tokyo Stock Exchange as 7860 and abbreviated as AGHD, is the holding company for a group of entertainment-related subsidiaries based in Japan." };
    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task2",
            "src/test/resources/OKE_Challenge/example_data/task2.ttl", false, ExperimentType.OKE_Task2);

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The extractor found everything and marked all entities using dbpedia
        // URIs (if they were available)
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(
                                        TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(
                                                        0,
                                                        12,
                                                        "http://dbpedia.org/resource/Brian_Banner",
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(
                                                        18,
                                                        35,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))),
                                                (Marking) new TypedNamedEntity(
                                                        28,
                                                        35,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))))),
                                new DocumentImpl(
                                        TEXTS[1],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(
                                                        0,
                                                        24,
                                                        "http://dbpedia.org/resource/AVEX_Records",
                                                        new HashSet<String>(
                                                                Arrays.asList(
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                                (Marking) new TypedNamedEntity(
                                                        109,
                                                        116,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))),
                                                (Marking) new TypedNamedEntity(
                                                        109,
                                                        116,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))))) }, GOLD_STD,
                        Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private Matching matching;

    public OKEChallengeTask2Test(Document[] annotatorResults, DatasetConfiguration dataset, Matching matching,
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
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(new TestOKETask2Annotator(
                Arrays.asList(annotatorResults)), dataset, ExperimentType.OKE_Task2, matching);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration, new F1MeasureTestingObserver(
                this, experimentTaskId, experimentDAO, expectedResults));
    }
}
