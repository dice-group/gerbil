package org.aksw.gerbil.annotator.impl.qa;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.http.HttpManagement;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskObserver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.connect.SocketConnection;

public class ExtendedQALDBasedWebServiceTest implements TaskObserver {

    private static final int FAST_SERVER_PORT = 8023;
    private static final String FAST_HTTP_SERVER_ADDRESS = "http://localhost:" + FAST_SERVER_PORT;

    private static final long MAX_WAITING_TIME = 20000;
    private static final long CHECK_INERVAL = 1000;

    private static long maxWaitingTimeBackup;
    private static long checkIntervalBackup;

    protected Semaphore taskEndedMutex = new Semaphore(0);
    private String correctAnswer = "http://dbpedia.org/resource/Michelle_Obama";

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

    private ExtendedDocumentReturningServerMock fastServerContainer;
    private ContainerServer fastServer;
    private SocketConnection fastConnection;

    @Before
    public void startServer() throws IOException {
        fastServerContainer = new ExtendedDocumentReturningServerMock();
        fastServer = new ContainerServer(fastServerContainer);
        fastConnection = new SocketConnection(fastServer);
        SocketAddress address1 = new InetSocketAddress(FAST_SERVER_PORT);
        fastConnection.connect(address1);
    }

    @Test
    public void correctResults() throws GerbilException, IOException {
        ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService(FAST_HTTP_SERVER_ADDRESS);
        Document document = new DocumentImpl();

        System.out.println("Testing now: Anything goes as excpected");
        document.setText("correct");

        List<Marking> results = service.answerQuestion(document, "en");
        AnswerSet<Object> answer = (AnswerSet<Object>) results.get(results.size() - 1);
        Object test = answer.getAnswers().iterator().next();
        if(test instanceof Annotation)
        	assertTrue(correctAnswer.equals(((Annotation)test).getUri()));
        if(test instanceof String)
        	assertTrue(correctAnswer.equals(test));
        System.out.println("Test done. Everything is ok");

        System.out.println("Testing now: Response Json is wrong");
        document = new DocumentImpl();
        document.setText("json");

        try {
            service.answerQuestion(document, "en");
        } catch (GerbilException e) {
            // assertTrue(e.getCause() instanceof JsonException);
        }
        System.out.println("Test done. Everything is ok");

        service.close();
    }

    @After
    public void stopServer() throws IOException {
        fastConnection.close();
        fastServer.stop();
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
