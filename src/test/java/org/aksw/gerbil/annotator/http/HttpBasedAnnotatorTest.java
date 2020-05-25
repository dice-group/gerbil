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
package org.aksw.gerbil.annotator.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.aksw.gerbil.Experimenter;
import org.aksw.gerbil.annotator.AnnotatorConfigurationImpl;
import org.aksw.gerbil.annotator.SingletonAnnotatorConfigImpl;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.InstanceListBasedDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.http.HttpManagement;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.test.SameAsRetrieverSingleton4Tests;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.aksw.simba.topicmodeling.concurrent.reporter.LogReporter;
import org.aksw.simba.topicmodeling.concurrent.reporter.Reporter;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class HttpBasedAnnotatorTest implements TaskObserver {

    private static final int FAST_SERVER_PORT = 8089;
    private static final String FAST_HTTP_SERVER_ADDRESS = "http://localhost:" + FAST_SERVER_PORT;
    private static final String FAST_ANNOTATOR_NAME = "fast annotator";
    private static final int SLOW_SERVER_PORT = 8090;
    private static final String SLOW_HTTP_SERVER_ADDRESS = "http://localhost:" + SLOW_SERVER_PORT;
    private static final String SLOW_ANNOTATOR_NAME = "slow annotator";

    private static final String TEXTS[] = new String[] {
            "Florence May Harding studied at a school in Sydney, and with Douglas Robert Dundas , but in effect had no formal training in either botany or art.",
            "Such notables include James Carville, who was the senior political adviser to Bill Clinton, and Donna Brazile, the campaign manager of the 2000 presidential campaign of Vice-President Al Gore.",
            "The senator received a Bachelor of Laws from the Columbia University." };
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1");
    private static final Document DOCUMENTS[] = new Document[] {
            new DocumentImpl(TEXTS[0], "http://www.aksw.org/gerbil/NifWebService/request_0",
                    Arrays.asList(
                            (Marking) new NamedEntity(0, 20,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Florence_May_Harding"),
                            (Marking) new NamedEntity(34, 6,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/National_Art_School"),
                            (Marking) new NamedEntity(44, 6,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Sydney"),
                            (Marking) new NamedEntity(61, 21,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas"))),
            new DocumentImpl(TEXTS[1], "http://www.aksw.org/gerbil/NifWebService/request_1",
                    Arrays.asList(
                            (Marking) new NamedEntity(22, 14,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/James_Carville"),
                            (Marking) new NamedEntity(57, 17,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Political_adviser"),
                            (Marking) new NamedEntity(78, 12,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Bill_Clinton"),
                            (Marking) new NamedEntity(96, 13,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Donna_Brazile"),
                            (Marking) new NamedEntity(115, 16,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Campaign_manager"),
                            (Marking) new NamedEntity(184, 7,
                                    "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Al_Gore"))),
            new DocumentImpl(TEXTS[2], "http://www.aksw.org/gerbil/NifWebService/request_2",
                    Arrays.asList((Marking) new NamedEntity(4, 7,
                            "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Senator_1"),
                    (Marking) new NamedEntity(49, 19,
                            "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Columbia_University"))) };

    private static final long MAX_WAITING_TIME = 2000;
    private static final long CHECK_INERVAL = 1000;
    private static final long SLOW_SERVER_WAITING_TIME = 10000;
    private static final long TEST_WAITING_TIME = 3 * DOCUMENTS.length * MAX_WAITING_TIME;
    private static final String EXPERIMENT_ID = "HttpBasedAnnotatorTest";

    private static final int NUMBER_OF_DATASETS = 3;

    private static long maxWaitingTimeBackup, checkIntervalBackup;

    protected WaitingDocumentReturningServerMock fastServerContainer;
    protected Server fastServer;
    protected Connection fastConnection;
    protected Server slowServer;
    protected Connection slowConnection;
    protected Semaphore taskEndedMutex = new Semaphore(0);

    public HttpBasedAnnotatorTest() {
    }

    @BeforeClass
    public static void setHttpConfig() {
        HttpManagement mngmt = HttpManagement.getInstance();
        maxWaitingTimeBackup = mngmt.getMaxWaitingTime();
        checkIntervalBackup = mngmt.getCheckInterval();
        mngmt.setMaxWaitingTime(MAX_WAITING_TIME);
        mngmt.setCheckInterval(CHECK_INERVAL);
    }

    @AfterClass
    public static void resetHttpConfig() {
        HttpManagement mngmt = HttpManagement.getInstance();
        mngmt.setMaxWaitingTime(maxWaitingTimeBackup);
        mngmt.setCheckInterval(checkIntervalBackup);
    }

    @Before
    public void startServer() throws IOException {
        fastServerContainer = new WaitingDocumentReturningServerMock(DOCUMENTS, 0);
        fastServer = new ContainerServer(fastServerContainer);
        fastConnection = new SocketConnection(fastServer);
        SocketAddress address1 = new InetSocketAddress(FAST_SERVER_PORT);
        fastConnection.connect(address1);
        slowServer = new ContainerServer(new WaitingDocumentReturningServerMock(DOCUMENTS, SLOW_SERVER_WAITING_TIME));
        slowConnection = new SocketConnection(slowServer);
        SocketAddress address2 = new InetSocketAddress(SLOW_SERVER_PORT);
        slowConnection.connect(address2);
    }

  /*  @Test
    public void test() throws NoSuchMethodException, SecurityException, InterruptedException {
        InstanceListBasedDataset datasets[] = new InstanceListBasedDataset[NUMBER_OF_DATASETS];
        for (int i = 0; i < datasets.length; ++i) {
            datasets[i] = new InstanceListBasedDataset("test dataset " + i, Arrays.asList(DOCUMENTS), ExperimentType.D2KB);
        }
        AnnotatorConfigurationImpl fastAnnotator = new AnnotatorConfigurationImpl(FAST_ANNOTATOR_NAME, false,
                NIFBasedAnnotatorWebservice.class.getConstructor(String.class, String.class),
                new Object[] { FAST_HTTP_SERVER_ADDRESS, FAST_ANNOTATOR_NAME }, ExperimentType.D2KB);
        AnnotatorConfigurationImpl slowAnnotator = new AnnotatorConfigurationImpl(SLOW_ANNOTATOR_NAME, false,
                NIFBasedAnnotatorWebservice.class.getConstructor(String.class, String.class),
                new Object[] { SLOW_HTTP_SERVER_ADDRESS, SLOW_ANNOTATOR_NAME }, ExperimentType.D2KB);
        ExperimentTaskConfiguration configs[] = new ExperimentTaskConfiguration[10 * NUMBER_OF_DATASETS];
        for (int i = 0; i < configs.length; ++i) {
            configs[i] = new ExperimentTaskConfiguration((((i & 1) == 0) ? fastAnnotator : slowAnnotator),
                    datasets[i / 10], ExperimentType.D2KB, Matching.WEAK_ANNOTATION_MATCH);
        }



        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        Overseer overseer = new SimpleOverseer();
        overseer.addObserver(this);
        @SuppressWarnings("unused")
        Reporter reporter = new LogReporter(overseer);
        Experimenter experimenter = new Experimenter(overseer, experimentDAO,
                SameAsRetrieverSingleton4Tests.getInstance(), new EvaluatorFactory(URI_KB_CLASSIFIER), configs,
                EXPERIMENT_ID);
        experimenter.run();

        // Try to wait for the tasks to finish
        Assert.assertTrue("Expected all experiments to have been finished.",
                taskEndedMutex.tryAcquire(configs.length, TEST_WAITING_TIME, TimeUnit.MILLISECONDS));

        List<ExperimentTaskStatus> results = experimentDAO.getResultsOfExperiment(EXPERIMENT_ID);
        for (ExperimentTaskStatus result : results) {
            Assert.assertFalse(result.annotator.equals(SLOW_ANNOTATOR_NAME));
            Assert.assertEquals(ExperimentDAO.TASK_FINISHED, result.state);
            Assert.assertEquals(1.0,(Double) result.getResultsMap().get("Micro F1 score").getResValue(), 0.000001);
        }
        // make sure that the fast server didn't throw anything (the slow server
        // might has thrown something)
        Assert.assertNull(fastServerContainer.getThrowable());
    }

   */

    /* @Test
    @Ignore
    public void testWithSingletonAnnotators() throws NoSuchMethodException, SecurityException, InterruptedException {
        InstanceListBasedDataset datasets[] = new InstanceListBasedDataset[NUMBER_OF_DATASETS];
        for (int i = 0; i < datasets.length; ++i) {
            datasets[i] = new InstanceListBasedDataset("test dataset " + i, Arrays.asList(DOCUMENTS), ExperimentType.D2KB);
        }
        AnnotatorConfigurationImpl fastAnnotator = new SingletonAnnotatorConfigImpl(FAST_ANNOTATOR_NAME, false,
                NIFBasedAnnotatorWebservice.class.getConstructor(String.class, String.class),
                new Object[] { FAST_HTTP_SERVER_ADDRESS, FAST_ANNOTATOR_NAME }, ExperimentType.D2KB);
        AnnotatorConfigurationImpl slowAnnotator = new SingletonAnnotatorConfigImpl(SLOW_ANNOTATOR_NAME, false,
                NIFBasedAnnotatorWebservice.class.getConstructor(String.class, String.class),
                new Object[] { SLOW_HTTP_SERVER_ADDRESS, SLOW_ANNOTATOR_NAME }, ExperimentType.D2KB);
        ExperimentTaskConfiguration configs[] = new ExperimentTaskConfiguration[2 * NUMBER_OF_DATASETS];
        for (int i = 0; i < configs.length; ++i) {
            configs[i] = new ExperimentTaskConfiguration((((i & 1) == 0) ? fastAnnotator : slowAnnotator),
                    datasets[i >> 1], ExperimentType.D2KB, Matching.WEAK_ANNOTATION_MATCH);
        }



        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        Overseer overseer = new SimpleOverseer();
        overseer.addObserver(this);
        @SuppressWarnings("unused")
        Reporter reporter = new LogReporter(overseer);
        Experimenter experimenter = new Experimenter(overseer, experimentDAO,
                SameAsRetrieverSingleton4Tests.getInstance(), new EvaluatorFactory(URI_KB_CLASSIFIER), configs,
                EXPERIMENT_ID);
        experimenter.run();

        // Try to wait for the tasks to finish
        Assert.assertTrue("Expected all experiments to have been finished.",
                taskEndedMutex.tryAcquire(configs.length, TEST_WAITING_TIME, TimeUnit.MILLISECONDS));

        List<ExperimentTaskStatus> results = experimentDAO.getResultsOfExperiment(EXPERIMENT_ID);
        for (ExperimentTaskStatus result : results) {
            Assert.assertFalse(result.annotator.equals(SLOW_ANNOTATOR_NAME));
            Assert.assertEquals(ExperimentDAO.TASK_FINISHED, result.state);
            Assert.assertEquals(1.0, (Double) result.getResultsMap().get("Micro F1 score").getResValue(), 0.000001);
        }
        // make sure that the fast server didn't throw anything (the slow server
        // might has thrown something)
        Assert.assertNull(fastServerContainer.getThrowable());
    }

     */

    @After
    public void stopServer() throws IOException {
        fastConnection.close();
        fastServer.stop();
        slowConnection.close();
        slowServer.stop();
    }

    @Override
    public void reportTaskFinished(Task task) {
        taskEndedMutex.release();
    }

    @Override
    public void reportTaskThrowedException(Task task, Throwable t) {
        taskEndedMutex.release();
    }
}
