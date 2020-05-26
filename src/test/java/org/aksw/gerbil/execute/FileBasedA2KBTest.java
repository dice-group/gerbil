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
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.impl.nif.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the entity linking evaluation.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class FileBasedA2KBTest extends AbstractExperimentTaskTest {

    private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task1",
            "src/test/resources/OKE_Challenge/example_data/task1.ttl", false, ExperimentType.A2KB, null, null);
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.A2KB;

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The results of the NERD-ML annotator
        testConfigs.add(new Object[] {
                "src/test/resources/annotator_examples/NERD_ML-OKE_2015_Task_1_example_set-w-A2KB.ttl", GOLD_STD,
                Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0.2666666667, 0.2222222222, 0.2424242424, 0.5714285714, 1.0 / 3.0, 0.4210526316, 0 },
                new double[] { 0.2222222222, 0.2222222222, 0.2222222222, 0.2857142857, 1.0 / 3.0, 0.3076923077, 0 } });
        // The results of the FOX annotator
        testConfigs
                .add(new Object[] { "src/test/resources/annotator_examples/FOX-OKE_2015_Task_1_example_set-w-A2KB.ttl",
                        GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                        new double[] { 0.8333333333, 0.5555555556, 0.6555555556, 0.7777777778, 0.5833333333,
                                0.6666666667, 0 },
                        new double[] { 0.8333333333, 0.5555555556, 0.6555555556, 0.7777777778, 0.5833333333,
                                0.6666666667, 0 } });
        // The results of the DBpedia Spotlight annotator
        testConfigs.add(new Object[] {
                "src/test/resources/annotator_examples/DBpedia_Spotlight-OKE_2015_Task_1_example_set-w-A2KB.ttl",
                GOLD_STD, Matching.WEAK_ANNOTATION_MATCH,
                new double[] { 0.75, 0.4722222222, 0.5722222222, 0.6666666667, 0.5, 0.5714285714, 0 },
                new double[] { 0.75, 0.4722222222, 0.5722222222, 0.6666666667, 0.5, 0.5714285714, 0 } });
        return testConfigs;
    }

    private String annotatorFileName;
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private double expectedResultWithoutConfidence[];
    private Matching matching;

    public FileBasedA2KBTest(String annotatorFileName, DatasetConfiguration dataset, Matching matching,
            double[] expectedResults, double[] expectedResultWithoutConfidence) {
        this.annotatorFileName = annotatorFileName;
        this.dataset = dataset;
        this.expectedResults = expectedResults;
        this.expectedResultWithoutConfidence = expectedResultWithoutConfidence;
        this.matching = matching;
    }

    @Test
    public void test() throws GerbilException {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();

        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                loadAnnotatorFile(annotatorFileName, false), dataset, EXPERIMENT_TYPE);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
    }

    @Test
    public void testWithoutConfidence() throws GerbilException {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();

        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                loadAnnotatorFile(annotatorFileName, true), dataset, EXPERIMENT_TYPE);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResultWithoutConfidence));
    }

    public AnnotatorConfiguration loadAnnotatorFile(String annotatorFileName, boolean eraseConfidenceValues)
            throws GerbilException {
        Dataset dataset = (new NIFFileDatasetConfig("ANNOTATOR", annotatorFileName, false, EXPERIMENT_TYPE, null, null))
                .getDataset(EXPERIMENT_TYPE);
        List<Document> instances;
        if (eraseConfidenceValues) {
            instances = new ArrayList<Document>(dataset.size());
            Document newDoc;
            for (Document originalDoc : dataset.getInstances()) {
                newDoc = new DocumentImpl();
                newDoc.setDocumentURI(originalDoc.getDocumentURI());
                newDoc.setText(originalDoc.getText());
                for (NamedEntity ne : originalDoc.getMarkings(NamedEntity.class)) {
                    newDoc.addMarking(new NamedEntity(ne.getStartPosition(), ne.getLength(), ne.getUris()));
                }
                instances.add(newDoc);
            }
        } else {
            instances = dataset.getInstances();
        }
        return new TestAnnotatorConfiguration(instances, ExperimentType.A2KB);
    }
}
