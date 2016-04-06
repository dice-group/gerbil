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
package org.aksw.gerbil.annotator.http;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.NIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.NIFDocumentParser;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class WaitingDocumentReturningServerMock implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitingDocumentReturningServerMock.class);

    private Map<String, Document> documents;
    private long waitingTime;
    private Throwable throwable;
    private NIFDocumentCreator nifCreator = new TurtleNIFDocumentCreator();
    private NIFDocumentParser nifParser = new TurtleNIFDocumentParser();

    public WaitingDocumentReturningServerMock(Document documents[], long waitingTime) {
        this.waitingTime = waitingTime;
        this.documents = new HashMap<String, Document>(documents.length * 2);
        for (int i = 0; i < documents.length; ++i) {
            this.documents.put(documents[i].getDocumentURI(), documents[i]);
        }
    }

    @Override
    public void handle(Request request, Response response) {
        Document requestDoc;
        try {
            requestDoc = nifParser.getDocumentFromNIFStream(request.getInputStream());
        } catch (Exception e) {
            response.setCode(Status.BAD_REQUEST.code);
            throwable = e;
            LOGGER.error("Couldn't get document from request. Aborting.", e);
            return;
        }
        String responseString;
        if (documents.containsKey(requestDoc.getDocumentURI())) {
            LOGGER.error("Couldn't find document with URI \"{}\". Returning request document.", requestDoc.getDocumentURI());
            responseString = nifCreator.getDocumentAsNIFString(documents.get(requestDoc.getDocumentURI()));
        } else {
            responseString = nifCreator.getDocumentAsNIFString(requestDoc);
        }
        OutputStream out = null;
        try {
            byte data[] = responseString.getBytes("UTF-8");
            response.setCode(Status.OK.code);
            response.setValue("Content-Type", nifCreator.getHttpContentType() + ";charset=utf-8");
            response.setContentLength(data.length);
            out = response.getOutputStream();

            if (waitingTime > 0) {
                try {
                    Thread.sleep(waitingTime);
                } catch (Exception e) {
                }
            }

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

}
