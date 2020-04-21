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
package org.aksw.gerbil.dataset.check;

import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpBasedEntityCheckerContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpBasedEntityCheckerContainer.class);

    public HttpBasedEntityCheckerContainer(boolean entityExists) {
        this.entityExists = entityExists;
    }

    private boolean entityExists;
    private Throwable throwable;

    @Override
    public void handle(Request request, Response response) {
        OutputStream out = null;
        try {
            response.setStatus(entityExists ? Status.OK : Status.NOT_FOUND);
            out = response.getOutputStream();
        } catch (Exception e) {
            LOGGER.error("Got exception.", e);
            if (throwable != null) {
                throwable = e;
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }
}