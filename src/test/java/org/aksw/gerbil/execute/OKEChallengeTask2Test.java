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
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.web.config.RootConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class OKEChallengeTask2Test extends AbstractExperimentTaskTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    private static final String TEXTS[] = new String[] {
            "Brian Banner is a fictional villain from the Marvel Comics Universe created by Bill Mantlo and Mike Mignola and first appearing in print in late 1985.",
            "Avex Group Holdings Inc., listed in the Tokyo Stock Exchange as 7860 and abbreviated as AGHD, is the holding company for a group of entertainment-related subsidiaries based in Japan." };
    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task2",
            "src/test/resources/OKE_Challenge/example_data/task2.ttl", false, ExperimentType.OKE_Task2, null, null);

    private static final SubClassInferencer inferencer = RootConfig.createSubClassInferencer();

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The annotator found everything and marked all classes correctly
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(0, 12,
                                                        "http://dbpedia.org/resource/Brian_Banner",
                                                        new HashSet<String>(Arrays.asList(
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
                                                (Marking) new TypedNamedEntity(18, 17,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))),
                                                (Marking) new TypedNamedEntity(28, 7,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                        new HashSet<String>(Arrays.asList(
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                (Marking) new TypedNamedEntity(109, 7,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))),
                                (Marking) new TypedNamedEntity(101, 15,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                        new HashSet<String>(
                                                Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))) },
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The annotator found everything and marked all classes correctly (but
        // with own URIs instead of the oke-challenge URIs)
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(0, 12,
                                                "http://dbpedia.org/resource/Brian_Banner",
                                                new HashSet<String>(Arrays.asList(
                                                        "http://www.aksw.org/createdClass/FictionalVillain",
                                                        "http://www.aksw.org/createdClass/Villain",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification"))),
                                (Marking) new TypedNamedEntity(18, 17,
                                        "http://www.aksw.org/createdClass/FictionalVillain",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))),
                                (Marking) new TypedNamedEntity(28, 7, "http://www.aksw.org/createdClass/Villain",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                        new HashSet<String>(
                                                Arrays.asList("http://www.aksw.org/createdClass/HoldingCompany",
                                                        "http://www.aksw.org/createdClass/Company",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                (Marking) new TypedNamedEntity(109, 7, "http://www.aksw.org/createdClass/Company",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))),
                                (Marking) new TypedNamedEntity(101, 15,
                                        "http://www.aksw.org/createdClass/HoldingCompany",
                                        new HashSet<String>(
                                                Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The annotator found everything but marked the most specific classes
        // only (which should still be correct)
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                Arrays.asList(
                                        (Marking) new TypedNamedEntity(0, 12,
                                                "http://dbpedia.org/resource/Brian_Banner",
                                                new HashSet<String>(Arrays.asList(
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification"))),
                                (Marking) new TypedNamedEntity(18, 17,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                        new HashSet<String>(Arrays.asList(
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                (Marking) new TypedNamedEntity(101, 15,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                        new HashSet<String>(
                                                Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The annotator found everything but did not use DOLCE classes
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(0, 12,
                                                        "http://dbpedia.org/resource/Brian_Banner",
                                                        new HashSet<String>(Arrays.asList(
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain"))),
                                                (Marking) new TypedNamedEntity(18, 17,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))),
                                                (Marking) new TypedNamedEntity(28, 7,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                        new HashSet<String>(Arrays.asList(
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company"))),
                                (Marking) new TypedNamedEntity(109, 7,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))),
                                (Marking) new TypedNamedEntity(101, 15,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                        new HashSet<String>(
                                                Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))) },
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0 } });
        // The annotator typed the classes correctly, but did not found the
        // classes inside the text
        testConfigs.add(new Object[] {
                new Document[] {
                        new DocumentImpl(TEXTS[0],
                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                Arrays.asList((Marking) new TypedNamedEntity(0, 12,
                                        "http://dbpedia.org/resource/Brian_Banner",
                                        new HashSet<String>(Arrays.asList(
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification"))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList((Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                new HashSet<String>(Arrays.asList(
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))))) },
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0 } });
        // The annotator typed the classes correctly, but did not found the
        // correct positions of the classes inside the text (yes, this is
        // different to the case above)
        testConfigs
                .add(new Object[] {
                        new Document[] {
                                new DocumentImpl(TEXTS[0],
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-1",
                                        Arrays.asList(
                                                (Marking) new TypedNamedEntity(0, 12,
                                                        "http://dbpedia.org/resource/Brian_Banner",
                                                        new HashSet<String>(Arrays.asList(
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person",
                                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Personification"))),
                                                (Marking) new TypedNamedEntity(0, 1,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/FictionalVillain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))),
                                                (Marking) new TypedNamedEntity(0, 1,
                                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Villain",
                                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(),
                                                                OWL.Class.getURI()))))),
                new DocumentImpl(TEXTS[1], "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/sentence-2",
                        Arrays.asList(
                                (Marking) new TypedNamedEntity(0, 24, "http://dbpedia.org/resource/AVEX_Records",
                                        new HashSet<String>(Arrays.asList(
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                                "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organization"))),
                                (Marking) new TypedNamedEntity(0, 1,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/Company",
                                        new HashSet<String>(Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))),
                                (Marking) new TypedNamedEntity(0, 1,
                                        "http://www.ontologydesignpatterns.org/data/oke-challenge/task-2/HoldingCompany",
                                        new HashSet<String>(
                                                Arrays.asList(RDFS.Class.getURI(), OWL.Class.getURI()))))) },
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH, new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0 } });
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
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.OKE_Task2), dataset,
                ExperimentType.OKE_Task2);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(inferencer), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
    }
}
