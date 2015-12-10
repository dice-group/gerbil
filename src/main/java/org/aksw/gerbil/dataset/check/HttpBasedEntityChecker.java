package org.aksw.gerbil.dataset.check;

import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpBasedEntityChecker extends AbstractHttpRequestEmitter implements EntityChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpBasedEntityChecker.class);

    public HttpBasedEntityChecker() {
    }

    public HttpBasedEntityChecker(CloseableHttpClient client) {
        super(client);
    }

    public HttpBasedEntityChecker(String name) {
        super(name);
    }

    public HttpBasedEntityChecker(String name, CloseableHttpClient client) {
        super(name, client);
    }

    @Override
    public boolean entityExists(String uri) {
        HttpHead request = null;
        try {
            request = createHeadRequest(uri);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Exception while creating HTTP request. Returning false.", e);
            return false;
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            return (status.getStatusCode() >= 200) && (status.getStatusCode() < 300);
        } catch (Exception e) {
            LOGGER.error("Exception while sending HTTP request. Returning false.", e);
            return false;
        } finally {
            closeRequest(request);
        }
    }

}
