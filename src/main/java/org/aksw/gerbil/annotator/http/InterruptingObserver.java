package org.aksw.gerbil.annotator.http;

import java.util.concurrent.Semaphore;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;

public class InterruptingObserver implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptingObserver.class);

    private long maxWaitingTime;
    private long checkInterval;
    private final ObjectLongOpenHashMap<ObservedHttpRequest> observedAnnotators = new ObjectLongOpenHashMap<ObservedHttpRequest>();
    private final Semaphore observedMappingMutex = new Semaphore(1);

    public InterruptingObserver(long maxWaitingTime, long checkInterval) {
        this.maxWaitingTime = maxWaitingTime;
        this.checkInterval = checkInterval;
    }

    @Override
    public void run() {
        while (true) {
            checkAnnotators();
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while sleeping.");
            }
        }
    }

    private void checkAnnotators() {
        try {
            observedMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Aborting.");
        }

        long waitingTime;
        long currentTime = System.currentTimeMillis();
        ObservedHttpRequest observedRequest;
        for (int i = 0; i < observedAnnotators.allocated.length; ++i) {
            if (observedAnnotators.allocated[i]) {
                waitingTime = currentTime - observedAnnotators.values[i];
                if (waitingTime > maxWaitingTime) {
                    observedRequest = ((ObservedHttpRequest) ((Object[]) observedAnnotators.keys)[i]);
                    LOGGER.info("The annotator \"{}\" already runs for {} ms. Trying to interrupt it.",
                            observedRequest.annotator.getName(), waitingTime);
                    try {
                        observedRequest.annotator.interrupt(observedRequest.request);
                    } catch (UnsupportedOperationException e) {
                        LOGGER.error("Couldn't interrupt request of annotator \"" + observedRequest.annotator.getName()
                                + "\" that is already running for " + waitingTime + " ms.");
                    }
                }
            }
        }
        observedMappingMutex.release();
    }

    public void reportStart(AbstractHttpBasedAnnotator annotator, HttpUriRequest request) {
        try {
            observedMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Aborting.");
        }
        observedAnnotators.put(new ObservedHttpRequest(request, annotator), System.currentTimeMillis());
        observedMappingMutex.release();
    }

    public void reportEnd(AbstractHttpBasedAnnotator annotator, HttpUriRequest request) {
        try {
            observedMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Aborting.");
        }
        observedAnnotators.remove(new ObservedHttpRequest(request, annotator));
        observedMappingMutex.release();
    }

    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }
}
