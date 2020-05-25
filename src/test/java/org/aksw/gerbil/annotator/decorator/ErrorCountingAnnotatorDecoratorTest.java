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
package org.aksw.gerbil.annotator.decorator;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.AbstractDatasetConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
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

   /* @Test
    public void testErrorCount() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(1, db, null, new EvaluatorFactory(),
                new ExperimentTaskConfiguration(new ErrorCausingAnnotatorConfig(5), new SimpleTestDatasetConfig(100),
                        ExperimentType.ERec, Matching.STRONG_ENTITY_MATCH));
        task.run();
        ExperimentTaskStatus result = db.getTaskResult(1);
        Assert.assertNotNull(result);
        int errCount = (Integer) result.getResultsMap().get("Error Count").getResValue();
        Assert.assertEquals(5, errCount);
        Assert.assertTrue(result.state >= 0);
    }

    */

  /*  @Test
    public void testTaskCanceling() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(2, db, null, new EvaluatorFactory(),
                new ExperimentTaskConfiguration(new ErrorCausingAnnotatorConfig(30), new SimpleTestDatasetConfig(1000),
                        ExperimentType.ERec, Matching.STRONG_ENTITY_MATCH));
        task.run();
        Assert.assertTrue(db.getExperimentState(2) < 0);
    }

   */



    public static class ErrorCausingAnnotator extends AbstractAnnotator implements EntityRecognizer {

        private int errorsPerHundred;
        private int errorsInThisHundred = 0;
        private int count = 0;

        public ErrorCausingAnnotator(int errorsPerHundred) {
            super("Error causing annotator");
            this.errorsPerHundred = errorsPerHundred;
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

  /*  public static class SimpleTestDatasetConfig extends AbstractDatasetConfiguration {

        private int size;

        public SimpleTestDatasetConfig(int size) {
            super("test dataset", false, ExperimentType.ERec, null, null);
            this.size = size;
        }

        @Override
        protected Dataset loadDataset() throws Exception {
            return new SimpleTestDataset(size);
        }

    }


   */
    public static class SimpleTestDataset extends AbstractDataset implements Dataset {

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
