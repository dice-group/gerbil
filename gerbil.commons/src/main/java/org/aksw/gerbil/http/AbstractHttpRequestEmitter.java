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

import java.io.IOException;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractHttpRequestEmitter implements HttpRequestEmitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpRequestEmitter.class);

    public static final String CONNECTION_ABORT_INDICATING_EXCPETION_MSG = "Software caused connection abort";

    protected CloseableHttpClient client;
    @Deprecated
    protected boolean closeClient = false;
    protected String name;

    public AbstractHttpRequestEmitter() {
        // this(null, HttpClientBuilder.create().build());
        // closeClient = true;
        this(null, HttpManagement.getInstance().getDefaultClient());
    }

    public AbstractHttpRequestEmitter(CloseableHttpClient client) {
        this(null, client);
    }

    public AbstractHttpRequestEmitter(String name) {
        // this(name, HttpClientBuilder.create().build());
        // closeClient = true;
        this(name, HttpManagement.getInstance().getDefaultClient());
    }

    public AbstractHttpRequestEmitter(String name, CloseableHttpClient client) {
        this.client = client;
        if (name == null) {
            this.name = this.getClass().getSimpleName();
        } else {
            this.name = name;
        }
    }

    public void interrupt(HttpUriRequest request) throws UnsupportedOperationException {
        request.abort();
    }

    /**
     * Creates a POST request and registers it with the {@link HttpManagement}
     * instance.
     * 
     * @param url
     *            the URL to which the request will be send
     * @return the request object
     * @throws IllegalArgumentException
     *             if the url is not valid
     */
    protected HttpPost createPostRequest(String url) throws IllegalArgumentException {
        HttpPost request = new HttpPost(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    /**
     * Creates a GET request and registers it with the {@link HttpManagement}
     * instance.
     * 
     * @param url
     *            the URL to which the request will be send
     * @return the request object
     * @throws IllegalArgumentException
     *             if the url is not valid
     */
    protected HttpGet createGetRequest(String url) throws IllegalArgumentException {
        HttpGet request = new HttpGet(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    /**
     * Creates a HEAD request and registers it with the {@link HttpManagement}
     * instance.
     * 
     * @param url
     *            the URL to which the request will be send
     * @return the request object
     * @throws IllegalArgumentException
     *             if the url is not valid
     */
    protected HttpHead createHeadRequest(String url) throws IllegalArgumentException {
        HttpHead request = new HttpHead(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    protected void closeRequest(HttpUriRequest request) {
        HttpManagement.getInstance().reportEnd(this, request);
    }

    protected CloseableHttpClient getClient() {
        return client;
    }

    protected void setClient(CloseableHttpClient client) {
        this.client = client;
    }

    protected boolean isCloseClient() {
        return closeClient;
    }

    @Deprecated
    protected void setCloseClient(boolean closeClient) {
        this.closeClient = closeClient;
    }

    protected CloseableHttpResponse sendRequest(HttpUriRequest request) throws GerbilException {
        return sendRequest(request, false);
    }

    protected CloseableHttpResponse sendRequest(HttpUriRequest request, boolean retry) throws GerbilException {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (NoHttpResponseException e) {
            if (retry) {
                LOGGER.warn("Got no response from the server (\"{}\"). Retrying...", e.getMessage());
                return sendRequest(request, false);
            } else {
                LOGGER.error("Got no response from the server.", e);
                throw new GerbilException("Got no response from the server.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } catch (RequestAbortedException e) {
            LOGGER.error("It seems like the annotator has needed too much time and has been interrupted.");
            throw new GerbilException("It seems like the annotator has needed too much time and has been interrupted.",
                    e, ErrorTypes.ANNOTATOR_NEEDED_TOO_MUCH_TIME);
        } catch (java.net.SocketException e) {
            if (e.getMessage().contains(CONNECTION_ABORT_INDICATING_EXCPETION_MSG)) {
                LOGGER.error("It seems like the annotator has needed too much time and has been interrupted.");
                throw new GerbilException(
                        "It seems like the annotator has needed too much time and has been interrupted.", e,
                        ErrorTypes.ANNOTATOR_NEEDED_TOO_MUCH_TIME);
            } else {
                LOGGER.error("Exception while sending request.", e);
                throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while sending request.", e);
            throw new GerbilException("Exception while sending request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        StatusLine status = response.getStatusLine();
        if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
            LOGGER.error("Response has the wrong status: " + status.toString());
            try {
                response.close();
            } catch (IOException e) {
            }
            throw new GerbilException("Response has the wrong status: " + status.toString(),
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        return response;
    }

    @Override
    public void close() throws IOException {
        // if (closeClient) {
        // client.close();
        // }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
