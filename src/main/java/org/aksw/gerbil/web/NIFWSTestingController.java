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
package org.aksw.gerbil.web;

import java.io.IOException;

import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This controller tests the connectivity of a NIF based web service with the
 * given URL.
 * 
 * @author Michael Röder
 * 
 */
@Controller
public class NIFWSTestingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFWSTestingController.class);

    private static final String RETURN_STATUS_NAME = "testOk";
    private static final String RETURN_ERROR_MSG_NAME = "errorMsg";
    private static final String NIF_WS_TEST_TEXT = "This simple text is for testing the communication between the given web service and the GERBIL web service.";

    @SuppressWarnings({ "unchecked", "deprecation" })
    @RequestMapping("/testNifWs")
    public @ResponseBody String testWebService(@RequestParam(value = "experimentType") String experimentType,
            @RequestParam(value = "url") String url) {
        LOGGER.info("Testing {} for an {} experiment.", url, experimentType);
        NIFBasedAnnotatorWebservice annotator = new NIFBasedAnnotatorWebservice(url, "TEST");
        JSONObject result = new JSONObject();
        // experimentType = experimentType.toUpperCase();
        Document document = new DocumentImpl();
        document.setText(NIF_WS_TEST_TEXT);
        try {
            switch (ExperimentType.valueOf(experimentType)) {

               /* case MT: {
                    annotator.performOKE2018Task4(document);
                    break;
                }

                */
            
            default: {
                throw new IllegalArgumentException("Got an unknown experiment type \"" + experimentType + "\".");
            }
            }
          //  result.put(RETURN_STATUS_NAME, true);
        } catch (Exception e) {
            LOGGER.error("Got an exception while testing {}. e = {}", url, e);
            result.put(RETURN_STATUS_NAME, false);
            result.put(RETURN_ERROR_MSG_NAME, e.getMessage());
        } finally {
            try {
                annotator.close();
            } catch (IOException e) {
            }
        }
        return JSONValue.toJSONString(result);
    }
}
