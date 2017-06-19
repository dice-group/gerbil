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

import org.aksw.gerbil.config.GerbilConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLongOpenHashMap;

public class HttpManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpManagement.class);

    public static final String MAXIMUM_TIME_TO_WAIT_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.maxWaitingTime";
    public static final String CHECK_INTERVAL_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.checkInterval";
    public static final String PROXY_HOST_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.proxyHost";
    public static final String PROXY_PORT_KEY = "org.aksw.gerbil.annotator.http.HttpManagement.proxyPort";
    /**
     * TODO move this list into the property files.
     */
    private static final String BLOCKING_DOMAINS[] = new String[] { "bg.dbpedia.org", "ca.dbpedia.org",
            "cs.dbpedia.org", "de.dbpedia.org", "dbpedia.org", "es.dbpedia.org", "eu.dbpedia.org", "fr.dbpedia.org",
            "hu.dbpedia.org", "id.dbpedia.org", "it.dbpedia.org", "ja.dbpedia.org", "ko.dbpedia.org", "nl.dbpedia.org",
            "pl.dbpedia.org", "pt.dbpedia.org", "ru.dbpedia.org", "tr.dbpedia.org", "bg.wikipedia.org",
            "ca.wikipedia.org", "cs.wikipedia.org", "de.wikipedia.org", "en.wikipedia.org", "es.wikipedia.org",
            "eu.wikipedia.org", "fr.wikipedia.org", "hu.wikipedia.org", "id.wikipedia.org", "it.wikipedia.org",
            "ja.wikipedia.org", "ko.wikipedia.org", "nl.wikipedia.org", "pl.wikipedia.org", "pt.wikipedia.org",
            "ru.wikipedia.org", "tr.wikipedia.org" };

    public static final long DEFAULT_WAITING_TIME = 60000;
    public static final long DEFAULT_CHECK_INTERVAL = 10000;
    public static final int DEFAULT_PROXY_PORT = 8080;
    /**
     * The time the system should wait before sending a new request to a domain
     * that could block the system.
     */
    private static final long BLOCKING_DOMAIN_WAITING_TIME = 500;

    private static final String INTERRUPTER_THREAD_NAME = "HttpInterrupter";
    private static final String USER_AGENT_STRING = "GERBIL/" + GerbilConfiguration.getGerbilVersion()
            + " (http://aksw.org/Projects/GERBIL.html)";

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

            instance = new HttpManagement(interruptingObserver, USER_AGENT_STRING);
            for (int i = 0; i < BLOCKING_DOMAINS.length; ++i) {
                instance.addBlockingDomain(BLOCKING_DOMAINS[i]);
            }
        }
        return instance;
    }

    protected InterruptingObserver interruptingObserver;
    protected CloseableHttpClient client;
    protected String userAgent;
    protected Semaphore blockingDomainMappingMutex = new Semaphore(1);
    protected ObjectLongOpenHashMap<String> blockingDomainTimestampMapping = new ObjectLongOpenHashMap<String>();

    protected HttpManagement(InterruptingObserver interruptingObserver, String userAgent) {
        this.interruptingObserver = interruptingObserver;
        this.client = generateHttpClientBuilder().build();
    }

    public void reportStart(HttpRequestEmitter emitter, HttpUriRequest request) {
        // get the permission to send
        getStartPermission(request);
        interruptingObserver.reportStart(emitter, request);
    }

    protected void getStartPermission(HttpUriRequest request) {
        try {
            blockingDomainMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex to access the list of blocking domains. Aborting.");
            return;
        }
        long timeToSleep = 0;
        try {
            String host = request.getURI().getHost();
            if ((host == null) || (!blockingDomainTimestampMapping.containsKey(host))) {
                return;
            }
            // we are allowed to use lget and lset since the mutex is securing
            // the hashmap
            long lastRequestTimeStamp = blockingDomainTimestampMapping.lget();
            long currentTime = System.currentTimeMillis();
            timeToSleep = BLOCKING_DOMAIN_WAITING_TIME - (currentTime - lastRequestTimeStamp);
            if (timeToSleep > 0) {
                blockingDomainTimestampMapping.lset(currentTime + timeToSleep);
            } else {
                blockingDomainTimestampMapping.lset(currentTime);
            }
        } finally {
            blockingDomainMappingMutex.release();
        }

        if (timeToSleep > 0) {
            try {
                Thread.sleep(BLOCKING_DOMAIN_WAITING_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for permission. Sending will start now.");
            }
        }
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

    public long getMaxWaitingTime() {
        return interruptingObserver.getMaxWaitingTime();
    }

    public long getCheckInterval() {
        return interruptingObserver.getCheckInterval();
    }

    public CloseableHttpClient getDefaultClient() {
        return client;
    }

    /**
     * Adds a domain that might block HTTP clients if they are sending too many
     * requests.
     */
    public void addBlockingDomain(String domain) {
        try {
            blockingDomainMappingMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for mutex to access the list of blocking domains. Aborting.");
            return;
        }
        try {
            blockingDomainTimestampMapping.put(domain, 0);
        } finally {
            blockingDomainMappingMutex.release();
        }
    }

    /**
     * Creates a HttpClientBuilder with the default settings of GERBIL.
     * 
     * @return a HttpClientBuilder with the default settings of GERBIL.
     */
    public HttpClientBuilder generateHttpClientBuilder() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setUserAgent(userAgent);

        String proxyHost = GerbilConfiguration.getInstance().getString(PROXY_HOST_KEY);
        int proxyPort = GerbilConfiguration.getInstance().getInt(PROXY_PORT_KEY, DEFAULT_PROXY_PORT);

        if (proxyHost != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            builder.setRoutePlanner(routePlanner);
        }

        return builder;
    }
}