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

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.TestDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleExperimentTaskTest extends AbstractExperimentTaskTest {

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(true);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
    }

    private static final List<Document> INSTANCES = Arrays.asList(
            (Document) new DocumentImpl(
                    "Angelina, her father Jon, and her partner Brad never played together in the same movie.",
                    "http://www.aksw.org/gerbil/test-document-1",
                    Arrays.asList((Marking) new NamedEntity(21, 3, "http://www.aksw.org/gerbil/test-document/Jon"),
                            (Marking) new NamedEntity(0, 8, "http://www.aksw.org/gerbil/test-document/Angelina"),
                            (Marking) new NamedEntity(42, 4, "http://www.aksw.org/gerbil/test-document/Brad"))),
            (Document) new DocumentImpl(
                    "McDonald’s Corp., which replaced its chief executive officer last week, saw U.S. sales drop 4 percent in February after a short-lived recovery in its domestic market sputtered.",
                    "http://www.aksw.org/gerbil/test-document-2",
                    Arrays.asList(
                            (Marking) new NamedEntity(0, 16, "http://www.aksw.org/gerbil/test-document/McDonaldsCorp"),
                            (Marking) new NamedEntity(76, 4, "http://www.aksw.org/gerbil/test-document/US"))));

    @Test
    public void testEntityRecognition() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(INSTANCES, ExperimentType.ERec),
                new TestDataset(INSTANCES, ExperimentType.ERec), ExperimentType.ERec, Matching.STRONG_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));

        experimentTaskId = 2;
        configuration = new ExperimentTaskConfiguration(new TestAnnotatorConfiguration(INSTANCES, ExperimentType.ERec),
                new TestDataset(INSTANCES, ExperimentType.ERec), ExperimentType.ERec, Matching.WEAK_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));

        experimentTaskId = 3;
        configuration = new ExperimentTaskConfiguration(new TestAnnotatorConfiguration(INSTANCES, ExperimentType.A2KB),
                new TestDataset(INSTANCES, ExperimentType.ERec), ExperimentType.ERec, Matching.STRONG_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));

        experimentTaskId = 4;
        configuration = new ExperimentTaskConfiguration(new TestAnnotatorConfiguration(INSTANCES, ExperimentType.A2KB),
                new TestDataset(INSTANCES, ExperimentType.ERec), ExperimentType.ERec, Matching.WEAK_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));
    }

    @Test
    public void testEntityLinking() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(INSTANCES, ExperimentType.D2KB),
                new TestDataset(INSTANCES, ExperimentType.D2KB), ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));

        experimentTaskId = 2;
        configuration = new ExperimentTaskConfiguration(new TestAnnotatorConfiguration(INSTANCES, ExperimentType.A2KB),
                new TestDataset(INSTANCES, ExperimentType.D2KB), ExperimentType.D2KB, Matching.STRONG_ENTITY_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));
    }

    @Test
    public void testEntityExtraction() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(INSTANCES, ExperimentType.A2KB),
                new TestDataset(INSTANCES, ExperimentType.A2KB), ExperimentType.A2KB, Matching.STRONG_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));

        experimentTaskId = 2;
        configuration = new ExperimentTaskConfiguration(new TestAnnotatorConfiguration(INSTANCES, ExperimentType.A2KB),
                new TestDataset(INSTANCES, ExperimentType.A2KB), ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new EverythingCorrectTestingObserver(this, experimentTaskId, experimentDAO));
    }

    protected static class EverythingCorrectTestingObserver extends F1MeasureTestingObserver {

        public EverythingCorrectTestingObserver(SimpleExperimentTaskTest testInstance, int experimentTaskId,
                SimpleLoggingResultStoringDAO4Debugging experimentDAO) {
            super(testInstance, experimentTaskId, experimentDAO, new double[] { 1, 1, 1, 1, 1, 1, 0 });
        }
    }
}
