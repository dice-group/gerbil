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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterruptingObserver implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptingObserver.class);

    private long maxWaitingTime;
    private long checkInterval;
    private final Map<ObservedHttpRequest, Long> observedRequests = new HashMap<>();

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
        synchronized (observedRequests) {
            long waitingTime;
            long currentTime = System.currentTimeMillis();
            ObservedHttpRequest observedRequest;
            Iterator<Entry<ObservedHttpRequest, Long>> iterator = observedRequests.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<ObservedHttpRequest, Long> entry = iterator.next();
                waitingTime = currentTime - entry.getValue();
                if (waitingTime > maxWaitingTime) {
                    observedRequest = entry.getKey();
                    if (observedRequest.request.isAborted()) {
                        LOGGER.warn(
                                "Observing an HTTP request that is already aborted. This could be mean that the HTTP management does not have been informed about the termination of a request.");
                        iterator.remove();
                    } else {
                        LOGGER.info("The HTTP request emitter \"{}\" already runs for {} ms. Trying to interrupt it.",
                                observedRequest.emitter.getName(), waitingTime);
                        try {
                            observedRequest.emitter.interrupt(observedRequest.request);
                        } catch (UnsupportedOperationException ex) {
                            LOGGER.error("Couldn't interrupt request of HTTP request emitter \""
                                    + observedRequest.emitter.getName() + "\" that is already running for " + waitingTime
                                    + " ms.");
                        }
                    }
                }
            }
        }
    }

    public void reportStart(HttpRequestEmitter emitter, HttpUriRequest request) {
        ObservedHttpRequest observedRequest = new ObservedHttpRequest(request, emitter);
        synchronized (observedRequests) {
            if (observedRequests.containsKey(observedRequest)) {
                LOGGER.error("There already is an observed request equal to this new one (" + observedRequest.toString()
                        + "). Note that this is a fatal error and the old request will be overwritten.");
            }
            observedRequests.put(observedRequest, System.currentTimeMillis());
        }
    }

    public void reportEnd(HttpRequestEmitter emitter, HttpUriRequest request) {
        ObservedHttpRequest observedRequest = new ObservedHttpRequest(request, emitter);
        synchronized (observedRequests) {
            if (observedRequests.containsKey(observedRequest)) {
                observedRequests.remove(observedRequest);
            } else {
                LOGGER.error("Tried to remove an observed request that is not existing (" + observedRequest.toString()
                        + "). This is a fatal error.");
            }
        }
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
