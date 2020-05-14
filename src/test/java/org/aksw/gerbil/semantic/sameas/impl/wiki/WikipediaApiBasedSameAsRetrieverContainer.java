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
package org.aksw.gerbil.semantic.sameas.impl.wiki;

import java.io.OutputStream;

import org.aksw.gerbil.utils.WikipediaHelper;
import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikipediaApiBasedSameAsRetrieverContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikipediaApiBasedSameAsRetrieverContainer.class);

    public WikipediaApiBasedSameAsRetrieverContainer(String uri, String expectedUri) {
        this.uri = uri;
        this.expectedUri = expectedUri;
    }

    private String uri;
    private String expectedUri;
    private Throwable throwable;

    @Override
    public void handle(Request request, Response response) {
        OutputStream out = null;
        try {
            byte data[] = buildXMLResponse(uri, expectedUri).getBytes("UTF-8");
            response.setStatus(Status.OK);
            response.setValue("Content-Type", "text/xml; charset=utf-8");
            response.setContentLength(data.length);
            out = response.getOutputStream();
            out.write(data);
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

    private String buildXMLResponse(String uri, String expectedUri) {
        StringBuilder response = new StringBuilder();
        String uriTitle = WikipediaHelper.getWikipediaTitle(uri);
        if(expectedUri == null) {
            response.append("<?xml version=\"1.0\"?><api batchcomplete=\"\"><query><pages><page _idx=\"0\""
                + " pageid=\"0\" ns=\"0\" title=\"" + uriTitle + "\" /></pages></query></api>");
        } else {
            String expectedUriTitle = WikipediaHelper.getWikipediaTitle(expectedUri);
            response.append("<?xml version=\"1.0\"?><api batchcomplete=\"\"><query><redirects><r from=\"" + uriTitle + "\"" 
                + " to=\"" + expectedUriTitle + "\" /></redirects><pages><page _idx=\"0\" pageid=\"0\" ns=\"0\""
                + "title=\"" + expectedUriTitle + "\" /></pages></query></api>");
        }
        return response.toString();
    }
}