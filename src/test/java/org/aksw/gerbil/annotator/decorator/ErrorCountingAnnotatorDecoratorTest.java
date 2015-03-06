package org.aksw.gerbil.annotator.decorator;

import it.acubelab.batframework.utils.AnnotationException;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotators.AbstractAnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datasets.AbstractDatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;

public class ErrorCountingAnnotatorDecoratorTest {

    @Test
    public void testErrorCount() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(1, db, new ExperimentTaskConfiguration(
                new ErrorCausingAnnotatorConfig(5), new SimpleTestDatasetConfig(100), ExperimentType.EntityRecognition,
                Matching.STRONG_ENTITY_MATCH));
        task.run();
        ExperimentTaskResult result = db.getTaskResult(1);
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.errorCount);
        Assert.assertTrue(result.state >= 0);
    }

    @Test
    public void testTaskCanceling() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(2, db, new ExperimentTaskConfiguration(
                new ErrorCausingAnnotatorConfig(30), new SimpleTestDatasetConfig(1000),
                ExperimentType.EntityRecognition, Matching.STRONG_ENTITY_MATCH));
        task.run();
        Assert.assertTrue(db.getExperimentState(2) < 0);
    }

    public static class ErrorCausingAnnotatorConfig extends AbstractAnnotatorConfiguration {

        private int errorsPerHundred;

        public ErrorCausingAnnotatorConfig(int errorsPerHundred) {
            super("Error causing topic system", false, ExperimentType.EntityRecognition);
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        protected Annotator loadAnnotator(ExperimentType type) throws Exception {
            return new ErrorCausingTopicSystem(errorsPerHundred);
        }

    }

    public static class ErrorCausingTopicSystem implements EntityRecognizer {

        private int errorsPerHundred;
        private int errorsInThisHundred = 0;
        private int count = 0;

        public ErrorCausingTopicSystem(int errorsPerHundred) {
            super();
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        public String getName() {
            return "Error causing topic system";
        }

        @Override
        public List<Span> performRecognition(Document document) {
            ++count;
            if (count > 100) {
                count -= 100;
                errorsInThisHundred = 0;
            }
            if (errorsInThisHundred < errorsPerHundred) {
                ++errorsInThisHundred;
                throw new AnnotationException("Test exception.");
            }
            return new ArrayList<Span>(0);
        }

    }

    public static class SimpleTestDatasetConfig extends AbstractDatasetConfiguration {

        private int size;

        public SimpleTestDatasetConfig(int size) {
            super("test dataset", false, ExperimentType.EntityRecognition);
            this.size = size;
        }

        @Override
        protected Dataset loadDataset() throws Exception {
            return new SimpleTestDataset(size);
        }

    }

    public static class SimpleTestDataset implements Dataset {

        private List<Document> instances;

        public SimpleTestDataset(int size) {
            instances = new ArrayList<Document>(size);
            for (int i = 0; i < size; ++i) {
                instances.add(new DocumentImpl("", Integer.toString(i), new ArrayList<Marking>(0)));
            }
        }

        @Override
        public int size() {
            return instances.size();
        }

        @Override
        public String getName() {
            return "test dataset";
        }

        @Override
        public List<Document> getInstances() {
            return instances;
        }

    }
}
