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

import org.aksw.gerbil.config.GerbilConfiguration;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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

    public synchronized static HttpManagement getInstance() {
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
    protected CloseableHttpClient client;

    protected HttpManagement(InterruptingObserver interruptingObserver) {
        this.interruptingObserver = interruptingObserver;
        this.client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    }

    public void reportStart(HttpRequestEmitter emitter, HttpUriRequest request) {
        interruptingObserver.reportStart(emitter, request);
    }

    public void reportEnd(HttpRequestEmitter emitter, HttpUriRequest request) {
        interruptingObserver.reportEnd(emitter, request);
    }

    public void setMaxWaitingTime(long maxWaitingTime) {
        interruptingObserver.setMaxWaitingTime(maxWaitingTime);
    }

    public void setCheckInterval(long checkInterval) {
        interruptingObserver.setCheckInterval(checkInterval);
    }

    public CloseableHttpClient getDefaultClient() {
        return client;
    }
}
