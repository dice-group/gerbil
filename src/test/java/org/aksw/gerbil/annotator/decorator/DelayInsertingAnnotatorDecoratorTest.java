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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecoratorTest.SimpleTestDatasetConfig;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.junit.Assert;
import org.junit.Test;

public class DelayInsertingAnnotatorDecoratorTest {

    @Test
    public void testDelay() throws InterruptedException {
        int configCount = 6;
        TestAnnotatorConfig config1 = new TestAnnotatorConfig("Test Annotator 1", 100, 100);
        TestAnnotatorConfig config2 = new TestAnnotatorConfig("Test Annotator 2", 50, 200);
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        List<ExperimentTask> tasks = new ArrayList<>(6);
        for (int i = 0; i < configCount; ++i) {
            tasks.add(new ExperimentTask(1, db, null, new EvaluatorFactory(),
                new ExperimentTaskConfiguration((i & 1) > 0 ? config1 : config2, new SimpleTestDatasetConfig(10),
                        ExperimentType.ERec, Matching.STRONG_ENTITY_MATCH)));
        }
        ExecutorService executor = Executors.newFixedThreadPool(configCount);
        for(ExperimentTask task : tasks) {
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        Assert.assertTrue(executor.isShutdown());
        Assert.assertEquals(0, config1.getErrors());
        Assert.assertEquals(0, config2.getErrors());
    }

    public static class TestAnnotatorConfig extends AbstractAdapterConfiguration
            implements AnnotatorConfiguration {

        private TestAnnotator instance;

        public TestAnnotatorConfig(String annotatorName, long delayBetweenCalls, long delayWithinMethod) {
            super(annotatorName, false, ExperimentType.ERec);
            instance = new TestAnnotator(annotatorName, delayBetweenCalls, delayWithinMethod);
        }

        @Override
        public Annotator getAnnotator(ExperimentType type) throws GerbilException {
            return instance;
        }

        public int getErrors() {
            return instance.getErrors();
        }

        @Override
        public long getDelay() {
            return instance.getDelayBetweenCalls();
        }
    }

    public static class TestAnnotator extends AbstractAnnotator implements EntityRecognizer {

        private long delayBetweenCalls;
        private long delayWithinMethod;
        private long lastMethodEnd = 0;
        private int errors = 0;
        
        public TestAnnotator(String annotatorName, long delayBetweenCalls, long delayWithinMethod) {
            super(annotatorName);
            this.delayBetweenCalls = delayBetweenCalls;
            this.delayWithinMethod = delayWithinMethod;
        }

        @Override
        public List<Span> performRecognition(Document document) {
            if ((lastMethodEnd + delayBetweenCalls) > System.currentTimeMillis()) {
                System.out.println((lastMethodEnd + delayBetweenCalls) + ">" + System.currentTimeMillis());
                ++errors;
            }
            try {
                Thread.sleep(delayWithinMethod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lastMethodEnd = System.currentTimeMillis();
            return new ArrayList<Span>(0);
        }

        public int getErrors() {
            return errors;
        }

        public long getDelayBetweenCalls() {
            return delayBetweenCalls;
        }
    }

}
