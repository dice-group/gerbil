package org.aksw.gerbil.annotator.http;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpManagement.class);

    public static final String MAXIMUM_TIME_TO_WAIT_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.maxWaitingTime";
    public static final String CHECK_INTERVAL_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.checkInterval";

    public static final long DEFAULT_WAITING_TIME = 60000;
    public static final long DEFAULT_CHECK_INTERVAL = 10000;

    private static final String INTERRUPTER_THREAD_NAME = "HttpInterrupter";

    private static HttpManagement instance;

    protected synchronized static HttpManagement getInstance() {
        if (instance == null) {
            long maxWaitingTime = DEFAULT_WAITING_TIME;
            try {
                maxWaitingTime = GerbilConfiguration.getInstance().getLong(MAXIMUM_TIME_TO_WAIT_KEY);
            } catch (Exception e) {
                LOGGER.warn("Couldn't load maximum time to wait from configuration. Using default "
                        + DEFAULT_WAITING_TIME + "ms.", e);
            }
            long checkInterval = DEFAULT_CHECK_INTERVAL;
            try {
                checkInterval = GerbilConfiguration.getInstance().getLong(CHECK_INTERVAL_KEY);
            } catch (Exception e) {
                LOGGER.warn("Couldn't load check interval from configuration. Using default " + DEFAULT_CHECK_INTERVAL
                        + "ms.", e);
            }
            InterruptingObserver interruptingObserver = new InterruptingObserver(maxWaitingTime, checkInterval);
            Thread t = new Thread(interruptingObserver);
            t.setDaemon(true);
            t.setName(INTERRUPTER_THREAD_NAME);
            t.start();

            instance = new HttpManagement(interruptingObserver);
        }
        return instance;
    }

    protected InterruptingObserver interruptingObserver;

    protected HttpManagement(InterruptingObserver interruptingObserver) {
        this.interruptingObserver = interruptingObserver;
    }

    public void reportStart(AbstractHttpBasedAnnotator annotator, HttpUriRequest request) {
        interruptingObserver.reportStart(annotator, request);
    }

    public void reportEnd(AbstractHttpBasedAnnotator annotator, HttpUriRequest request) {
        interruptingObserver.reportEnd(annotator, request);
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        interruptingObserver.setMaxWaitingTime(maxWaitingTime);
    }

    public void setCheckInterval(long checkInterval) {
        interruptingObserver.setCheckInterval(checkInterval);
    }
}
