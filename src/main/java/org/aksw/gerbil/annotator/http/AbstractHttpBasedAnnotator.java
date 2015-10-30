package org.aksw.gerbil.annotator.http;

import java.io.IOException;

import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpBasedAnnotator extends AbstractAnnotator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpBasedAnnotator.class);

    public static final String CONNECTION_ABORT_INDICATING_EXCPETION_MSG = "Software caused connection abort";

    protected CloseableHttpClient client;
    protected boolean closeClient = false;

    public AbstractHttpBasedAnnotator() {
        this(HttpClients.createDefault());
        closeClient = true;
    }

    public AbstractHttpBasedAnnotator(CloseableHttpClient client) {
        this.client = client;
    }

    public AbstractHttpBasedAnnotator(String name) {
        this(name, HttpClients.createDefault());
        closeClient = true;
    }

    public AbstractHttpBasedAnnotator(String name, CloseableHttpClient client) {
        super(name);
        this.client = client;
    }

    public void interrupt(HttpUriRequest request) throws UnsupportedOperationException {
        request.abort();
    }

    protected HttpPost createPostRequest(String url) {
        HttpPost request = new HttpPost(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    protected HttpGet createGetRequest(String url) {
        HttpGet request = new HttpGet(url);
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

    protected void setCloseClient(boolean closeClient) {
        this.closeClient = closeClient;
    }

    protected CloseableHttpResponse sendRequest(HttpUriRequest request) throws GerbilException {
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
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
        if (closeClient) {
            client.close();
        }
    }
}
