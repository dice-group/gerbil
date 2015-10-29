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
package org.aksw.gerbil.execute;

import java.util.concurrent.Semaphore;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.aksw.simba.topicmodeling.concurrent.reporter.LogReporter;
import org.aksw.simba.topicmodeling.concurrent.reporter.Reporter;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExperimentTaskTest {

    private Throwable testError = null;
    private Semaphore mutex = new Semaphore(0);

    public void runTest(int experimentTaskId, ExperimentDAO experimentDAO, EvaluatorFactory evFactory,
            ExperimentTaskConfiguration configuration, TaskObserver observer) {
        ExperimentTask task = new ExperimentTask(experimentTaskId, experimentDAO, evFactory, configuration);
        Overseer overseer = new SimpleOverseer();
        overseer.addObserver(observer);
        @SuppressWarnings("unused")
        Reporter reporter = new LogReporter(overseer);
        overseer.startTask(task);
        // wait for the task to end
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNull("Got an exception: " + testError, testError);
    }

    protected static abstract class AbstractJUnitTestTaskObserver implements TaskObserver {

        private static final Logger LOGGER = LoggerFactory
                .getLogger(AbstractExperimentTaskTest.AbstractJUnitTestTaskObserver.class);

        private AbstractExperimentTaskTest testInstance;

        public AbstractJUnitTestTaskObserver(AbstractExperimentTaskTest testInstance) {
            this.testInstance = testInstance;
        }

        @Override
        public void reportTaskThrowedException(Task task, Throwable t) {
            testInstance.testError = t;
            LOGGER.error("Got an unexpected exception.", t);
            // If there was an error we have to release the mutex here
            testInstance.mutex.release();
        }

        @Override
        public void reportTaskFinished(Task task) {
            testTaskResults(task);
            // If there was no error we have to release the mutex here
            testInstance.mutex.release();
        }

        protected abstract void testTaskResults(Task task);

    }

    protected static class F1MeasureTestingObserver extends AbstractJUnitTestTaskObserver {

        public static int MACRO_PREC_INDEX = 0;
        public static int MACRO_REC_INDEX = 1;
        public static int MACRO_F1_INDEX = 2;
        public static int MICRO_PREC_INDEX = 3;
        public static int MICRO_REC_INDEX = 4;
        public static int MICRO_F1_INDEX = 5;
        public static int ERROR_COUNT_INDEX = 6;

        private static final double DELTA = 0.0000001;

        private int experimentTaskId;
        private SimpleLoggingResultStoringDAO4Debugging experimentDAO;
        private double expectedResults[];

        public F1MeasureTestingObserver(AbstractExperimentTaskTest testInstance, int experimentTaskId,
                SimpleLoggingResultStoringDAO4Debugging experimentDAO, double expectedResults[]) {
            super(testInstance);
            this.experimentTaskId = experimentTaskId;
            this.experimentDAO = experimentDAO;
            this.expectedResults = expectedResults;
        }

        @Override
        protected void testTaskResults(Task task) {
            Assert.assertEquals(ExperimentDAO.TASK_FINISHED, experimentDAO.getExperimentState(experimentTaskId));
            ExperimentTaskResult result = experimentDAO.getTaskResult(experimentTaskId);
            Assert.assertEquals(expectedResults[MACRO_PREC_INDEX], result.getMacroPrecision(), DELTA);
            Assert.assertEquals(expectedResults[MACRO_REC_INDEX], result.getMacroRecall(), DELTA);
            Assert.assertEquals(expectedResults[MACRO_F1_INDEX], result.getMacroF1Measure(), DELTA);
            Assert.assertEquals(expectedResults[MICRO_PREC_INDEX], result.getMicroPrecision(), DELTA);
            Assert.assertEquals(expectedResults[MICRO_REC_INDEX], result.getMicroRecall(), DELTA);
            Assert.assertEquals(expectedResults[MICRO_F1_INDEX], result.getMicroF1Measure(), DELTA);
            Assert.assertEquals(expectedResults[ERROR_COUNT_INDEX], result.getErrorCount(), DELTA);
        }
    }
}
