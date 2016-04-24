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
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.DatasetConfigurationImpl;
import org.aksw.gerbil.dataset.impl.qald.FileBasedQALDDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.qa.QALDStreamType;
import org.aksw.gerbil.qa.datatypes.AnswerType;
import org.aksw.gerbil.qa.datatypes.AnswerTypes;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the AT2KB evaluation.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class AT2KBTest extends AbstractExperimentTaskTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() throws NoSuchMethodException, SecurityException {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    protected static DatasetConfiguration generateConfig() {
        try {
            return new DatasetConfigurationImpl("QALD_Test_dataset", false,
                    FileBasedQALDDataset.class.getConstructor(String.class, String.class, QALDStreamType.class),
                    new Object[] { "QALD_Test_dataset", "src/test/resources/datasets/QALD_test.json",
                            QALDStreamType.JSON },
                    ExperimentType.QA, null, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String TEXTS[] = new String[] { "Which German cities have more than 250000 inhabitants?", };
    private static final DatasetConfiguration GOLD_STD = generateConfig();
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The system returns nothing
        testConfigs.add(new Object[] { new Document[] {}, GOLD_STD, Matching.STRONG_ENTITY_MATCH,
                new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The system returns a correct answer type
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "http://qa.gerbil.aksw.org/QALD_Test_dataset/question#101",
                        Arrays.asList((Marking) new AnswerType(AnswerTypes.RESOURCE))) },
                GOLD_STD, Matching.STRONG_ENTITY_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
        // The system returns a wrong answer type
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "http://qa.gerbil.aksw.org/QALD_Test_dataset/question#101",
                        Arrays.asList((Marking) new AnswerType(AnswerTypes.BOOLEAN))) },
                GOLD_STD, Matching.STRONG_ENTITY_MATCH, new double[] { 0, 0, 0, 0, 0, 0, 0 } });
        // The system returns a two answer types
        testConfigs.add(new Object[] {
                new Document[] { new DocumentImpl(TEXTS[0], "http://qa.gerbil.aksw.org/QALD_Test_dataset/question#101",
                        Arrays.asList((Marking) new AnswerType(AnswerTypes.RESOURCE),
                                (Marking) new AnswerType(AnswerTypes.BOOLEAN))) },
                GOLD_STD, Matching.STRONG_ENTITY_MATCH, new double[] { 0.5, 1.0, 2d / 3d, 0.5, 1.0, 2d / 3d, 0 } });
        return testConfigs;
    }

    private Document annotatorResults[];
    private DatasetConfiguration dataset;
    private double expectedResults[];
    private Matching matching;

    public AT2KBTest(Document[] annotatorResults, DatasetConfiguration dataset, Matching matching,
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
                new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.QA), dataset,
                ExperimentType.QA, matching);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                new F1MeasureTestingObserver(this, ExperimentType.AType, experimentTaskId, experimentDAO,
                        expectedResults));
    }
}
