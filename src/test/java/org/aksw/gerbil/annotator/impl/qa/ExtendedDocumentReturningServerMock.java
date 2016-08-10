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
package org.aksw.gerbil.annotator.impl.qa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.aksw.commons.util.Files;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.Ignore;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ExtendedDocumentReturningServerMock implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedDocumentReturningServerMock.class);
  
    private static final String CORRECT_JSON_FILENAME = "src/test/resources/mock/extJson_test.json";
    
    private Throwable throwable;

    public ExtendedDocumentReturningServerMock(){
    	
    }
    
    public String getCorrectJSON() throws IOException{
    	return Files.readContent(new File(CORRECT_JSON_FILENAME));
    }

    public String getWrongJSON(){
    	return "[\"simply wrong\":\"asd\"]";
    }

	@Override
    public void handle(Request request, Response response) {
        String content=null;
		try {
			content = request.getContent();
		} catch (IOException e) {
			 LOGGER.error("Got exception.", e);
		}
        OutputStream out = null;
        try {
        	byte data[]="xmasdöl".getBytes();
        	if(content.equals("query=json")){
        		data = getWrongJSON().getBytes("UTF-8");
        	}
        	else if(content.equals("query=correct")){
        		data = getCorrectJSON().getBytes("UTF-8");
        	}
        	
            
            response.setCode(Status.OK.code);
            if(content.equals("query=contentType")){
            	response.setValue("Content-Type", ContentType.APPLICATION_XML + ";charset=utf-8");
            }
            else {
            	response.setValue("Content-Type", ContentType.APPLICATION_JSON + ";charset=utf-8");
            }
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

}
