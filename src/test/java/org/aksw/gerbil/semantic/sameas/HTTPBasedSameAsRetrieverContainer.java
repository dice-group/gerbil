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
package org.aksw.gerbil.semantic.sameas;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RDFLanguages;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPBasedSameAsRetrieverContainer implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPBasedSameAsRetrieverContainer.class);

    private static final String RESPONSE_HEADER_VALUE = RDFLanguages.RDFXML.getContentType().getContentType();

    private String uri;
    private Collection<String> expectedURIs;
    private Throwable throwable;

    public HTTPBasedSameAsRetrieverContainer(String uri, Collection<String> expectedURIs) {
        this.uri = uri;
        this.expectedURIs = expectedURIs;
    }

    @Override
    public void handle(Request request, Response response) {
        if (uri != null && request.getTarget().startsWith("/resource/")) {
            OutputStream out = null;
            try {
                byte data[] = buildXMLResponse(uri, expectedURIs).getBytes("UTF-8");
                response.setStatus(Status.OK);
                response.setValue("Content-Type", RESPONSE_HEADER_VALUE + " ;charset=UTF-8");
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
        } else {
            response.setStatus(Status.NOT_FOUND);
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
            }
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    private String buildXMLResponse(String uri, Collection<String> expectedURIs) {
        if(expectedURIs == null) {
            expectedURIs = new HashSet<String>();
        }
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n" +
                    "<rdf:RDF \n" +
                        "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
                        "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" > \n" +
                    "<rdf:Description rdf:about=\"" + uri + "\"> \n");

        for(String expectedURI: expectedURIs) {
            xml.append("<owl:sameAs rdf:resource=\"" + expectedURI + "\" /> \n");
        }
        xml.append("</rdf:Description> \n" + "</rdf:RDF> \n");
        return xml.toString();
    }
}