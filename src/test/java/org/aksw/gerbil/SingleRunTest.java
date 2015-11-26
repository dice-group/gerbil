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
package org.aksw.gerbil;

import java.io.Closeable;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.MultipleSameAsRetriever;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.aksw.simba.topicmodeling.concurrent.overseers.pool.DefeatableOverseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class SingleRunTest implements TaskObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleRunTest.class);

    private static final String ANNOTATOR_NAME = "DBpedia Spotlight";
    private static final String DATASET_NAME = "MSNBC";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

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
        SameAsRetriever retriever = RootConfig.createSameAsRetriever();
        adapterManager.setDatasets(DatasetsConfig.datasets(RootConfig.getEntityCheckerManager(), retriever));

        AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANNOTATOR_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(annotatorConfig);
        DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig(DATASET_NAME, EXPERIMENT_TYPE);
        Assert.assertNotNull(datasetConfig);

        DefeatableOverseer overseer = RootConfig.createOverseer();
        overseer.addObserver(this);

        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, EXPERIMENT_TYPE, MATCHING) };

        Experimenter experimenter = new Experimenter(overseer, new SimpleLoggingDAO4Debugging(), retriever,
                new EvaluatorFactory(), taskConfigs, "SingleRunTest");
        experimenter.run();

        mutex.acquire();

        closeHttpRetriever(retriever);
        overseer.shutdown();
    }

    private void closeHttpRetriever(SameAsRetriever retriever) {
        if (retriever instanceof SameAsRetrieverDecorator) {
            closeHttpRetriever(((SameAsRetrieverDecorator) retriever).getDecorated());
        } else if (retriever instanceof MultipleSameAsRetriever) {
            for (SameAsRetriever decorated : ((MultipleSameAsRetriever) retriever).getRetriever()) {
                closeHttpRetriever(decorated);
            }
        }
        if (retriever instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) retriever);
        }
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
