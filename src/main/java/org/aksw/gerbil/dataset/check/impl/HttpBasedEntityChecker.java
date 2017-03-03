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
package org.aksw.gerbil.dataset.check.impl;

import org.aksw.gerbil.dataset.check.EntityChecker;
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Exception while creating HTTP request. Returning false.", e);
                // } else {
                // LOGGER.error("Exception while creating HTTP request.
                // Returning false. Exception: "
                // + e.getLocalizedMessage());
            }
            return false;
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            return (status.getStatusCode() >= 200) && (status.getStatusCode() < 300);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Exception while sending HTTP request. Returning false.", e);
                // } else {
                // LOGGER.error(
                // "Exception while sending HTTP request. Returning false.
                // Exception: " + e.getLocalizedMessage());
            }
            return false;
        } finally {
            closeRequest(request);
        }
    }

}
