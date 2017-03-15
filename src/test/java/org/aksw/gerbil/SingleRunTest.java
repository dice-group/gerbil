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
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.FileBasedCachingEntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.MultipleSameAsRetriever;
import org.aksw.gerbil.test.EntityCheckerManagerSingleton4Tests;
import org.aksw.gerbil.test.SameAsRetrieverSingleton4Tests;
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


    private static final String ANNOTATOR_NAME = "NIFWS_QANARY(http://qanary.univ-st-etienne.fr/gerbil)";
    private static final String DATASET_NAME = "QALD4 Train Hybrid";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.QA;

    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

    private static final boolean USE_SAME_AS_RETRIEVAL = false;
    private static final boolean USE_ENTITY_CHECKING = false;

    private static final SameAsRetriever SAME_AS_RETRIEVER = USE_SAME_AS_RETRIEVAL
            ? SameAsRetrieverSingleton4Tests.getInstance() : null;
    private static final EntityCheckerManager ENTITY_CHECKER_MANAGER = USE_ENTITY_CHECKING
            ? EntityCheckerManagerSingleton4Tests.getInstance() : null;

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
        DefeatableOverseer overseer = null;
        try {
            AdapterManager adapterManager = new AdapterManager();
            adapterManager.setAnnotators(AnnotatorsConfig.annotators());
            adapterManager.setDatasets(DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER, SAME_AS_RETRIEVER));

            AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANNOTATOR_NAME, EXPERIMENT_TYPE, "en");
            Assert.assertNotNull(annotatorConfig);
            DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig(DATASET_NAME, EXPERIMENT_TYPE, "en");
            Assert.assertNotNull(datasetConfig);
            // DatasetConfiguration datasetConfig = new
            // DatasetConfigurationImpl("Test", false,
            // FileBasedQALDDataset.class.getConstructor(String.class,
            // String.class, QALDStreamType.class),
            // new Object[] { "TEST",
            // "src/test/resources/datasets/QALD_test.xml", QALDStreamType.XML
            // },
            // EXPERIMENT_TYPE, ENTITY_CHECKER_MANAGER, SAME_AS_RETRIEVER);

            overseer = RootConfig.createOverseer();
            overseer.addObserver(this);

            ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                    new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, EXPERIMENT_TYPE, MATCHING) };

            Experimenter experimenter = new Experimenter(overseer, new SimpleLoggingDAO4Debugging(), SAME_AS_RETRIEVER,
                    new EvaluatorFactory(), taskConfigs, "SingleRunTest");
            experimenter.run();

            mutex.acquire();
        } finally {
            closeHttpRetriever(SAME_AS_RETRIEVER);
            if (overseer != null) {
                overseer.shutdown();
            }
            if ((ENTITY_CHECKER_MANAGER != null)
                    && (ENTITY_CHECKER_MANAGER instanceof FileBasedCachingEntityCheckerManager)) {
                ((FileBasedCachingEntityCheckerManager) ENTITY_CHECKER_MANAGER).storeCache();
            }
        }
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
