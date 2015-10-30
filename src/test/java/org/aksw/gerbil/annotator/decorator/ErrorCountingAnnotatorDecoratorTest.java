/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.annotator.decorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datasets.AbstractDatasetConfiguration;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;

import it.unipi.di.acube.batframework.utils.AnnotationException;

public class ErrorCountingAnnotatorDecoratorTest {

    @Test
    public void testErrorCount() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(1, db, new EvaluatorFactory(),
                new ExperimentTaskConfiguration(new ErrorCausingAnnotatorConfig(5), new SimpleTestDatasetConfig(100),
                        ExperimentType.ERec, Matching.STRONG_ENTITY_MATCH));
        task.run();
        ExperimentTaskResult result = db.getTaskResult(1);
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.errorCount);
        Assert.assertTrue(result.state >= 0);
    }

    @Test
    public void testTaskCanceling() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(2, db, new EvaluatorFactory(),
                new ExperimentTaskConfiguration(new ErrorCausingAnnotatorConfig(30), new SimpleTestDatasetConfig(1000),
                        ExperimentType.ERec, Matching.STRONG_ENTITY_MATCH));
        task.run();
        Assert.assertTrue(db.getExperimentState(2) < 0);
    }

    public static class ErrorCausingAnnotatorConfig extends AbstractAdapterConfiguration
            implements AnnotatorConfiguration {

        private int errorsPerHundred;

        public ErrorCausingAnnotatorConfig(int errorsPerHundred) {
            super("Error causing topic system", false, ExperimentType.ERec);
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        public Annotator getAnnotator(ExperimentType type) throws GerbilException {
            return new ErrorCausingAnnotator(errorsPerHundred);
        }
    }

    public static class ErrorCausingAnnotator implements EntityRecognizer {

        private int errorsPerHundred;
        private int errorsInThisHundred = 0;
        private int count = 0;

        public ErrorCausingAnnotator(int errorsPerHundred) {
            super();
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        public String getName() {
            return "Error causing annotator";
        }

        @Override
        public void setName(String name) {
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

        @Override
        public void close() throws IOException {
        }

    }

    public static class SimpleTestDatasetConfig extends AbstractDatasetConfiguration {

        private int size;

        public SimpleTestDatasetConfig(int size) {
            super("test dataset", false, ExperimentType.ERec);
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

        @Override
        public void setName(String name) {
        }

    }
}
