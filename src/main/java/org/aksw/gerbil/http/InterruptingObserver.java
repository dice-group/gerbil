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
package org.aksw.gerbil.http;

import java.util.concurrent.Semaphore;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;

public class InterruptingObserver implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptingObserver.class);

    private long maxWaitingTime;
    private long checkInterval;
    private final ObjectLongOpenHashMap<ObservedHttpRequest> observedRequests = new ObjectLongOpenHashMap<ObservedHttpRequest>();
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
        for (int i = 0; i < observedRequests.allocated.length; ++i) {
            if (observedRequests.allocated[i]) {
                waitingTime = currentTime - observedRequests.values[i];
                if (waitingTime > maxWaitingTime) {
                    observedRequest = ((ObservedHttpRequest) ((Object[]) observedRequests.keys)[i]);
                    LOGGER.info("The HTTP request emitter \"{}\" already runs for {} ms. Trying to interrupt it.",
                            observedRequest.emitter.getName(), waitingTime);
                    try {
                        observedRequest.emitter.interrupt(observedRequest.request);
                    } catch (UnsupportedOperationException e) {
                        LOGGER.error("Couldn't interrupt request of HTTP request emitter \""
                                + observedRequest.emitter.getName() + "\" that is already running for " + waitingTime
                                + " ms.");
                    }
                }
            }
        }
        observedMappingMutex.release();
    }

    public void reportStart(HttpRequestEmitter emitter, HttpUriRequest request) {
        ObservedHttpRequest observedRequest = new ObservedHttpRequest(request, emitter);
        try {
            observedMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Aborting.");
        }
        if (observedRequests.containsKey(observedRequest)) {
            LOGGER.error("There already is an observed request equal to this new one (" + observedRequest.toString()
                    + "). Note that this is a fatal error and the old request will be overwritten.");
        }
        observedRequests.put(observedRequest, System.currentTimeMillis());
        observedMappingMutex.release();
    }

    public void reportEnd(HttpRequestEmitter emitter, HttpUriRequest request) {
        ObservedHttpRequest observedRequest = new ObservedHttpRequest(request, emitter);
        try {
            observedMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex. Aborting.");
        }
        if (observedRequests.containsKey(observedRequest)) {
            observedRequests.remove(observedRequest);
        } else {
            LOGGER.error("Tried to remove an observed request that is not existing (" + observedRequest.toString()
                    + "). This is a fatal error.");
        }
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
