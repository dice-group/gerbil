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
package org.aksw.gerbil;

import java.util.concurrent.Semaphore;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SingleRunTest implements TaskObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleRunTest.class);

    private static final String ANNOTATOR_NAME = "FOX";
    private static final String DATASET_NAME = "N3-Reuters-128";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    private static final Matching MATCHING = Matching.WEAK_ANNOTATION_MATCH;

    public static void main(String[] args) throws Exception {
        SingleRunTest test = new SingleRunTest();
        test.run();
    }

    private Semaphore mutex = new Semaphore(0);

    @Test
    public void runTest() throws Exception {
        run();
    }

    public void run() throws Exception {
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
        adapterManager.setDatasets(DatasetsConfig.datasets());

        AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANNOTATOR_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(annotatorConfig);
        DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig(DATASET_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(datasetConfig);

        DefeatableOverseer overseer = RootConfig.createOverseer();
        overseer.addObserver(this);

        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, EXPERIMENT_TYPE, MATCHING) };

        Experimenter experimenter = new Experimenter(overseer, new SimpleLoggingDAO4Debugging(), new EvaluatorFactory(),
                taskConfigs, "SingleRunTest");
        experimenter.run();

        mutex.acquire();

        overseer.shutdown();
    }

    @Override
    public void reportTaskFinished(Task task) {
        mutex.release();
    }

    @Override
    public void reportTaskThrowedException(Task task, Throwable t) {
        LOGGER.error("Task throwed exception.", t);
        Assert.assertNull(t);
        mutex.release();
    }
}
