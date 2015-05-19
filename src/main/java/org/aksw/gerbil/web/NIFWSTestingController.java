/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.web;

import it.acubelab.batframework.systemPlugins.DBPediaApi;

import org.aksw.gerbil.bat.annotator.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This controller tests the connectivity of a NIF based web service with the given URL.
 * 
 * @author Michael Röder
 * 
 */
@Controller
public class NIFWSTestingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFWSTestingController.class);

    private static final String RETURN_STATUS_NAME = "testOk";
    private static final String RETURN_ERROR_MSG_NAME = "errorMsg";
    private static final String NIF_WS_TEST_TEXT = "This simple text is for texting the communication between the given web service and the GERBIL web service.";

    @SuppressWarnings("unchecked")
    @RequestMapping("/testNifWs")
    public @ResponseBody
    String testWebService(@RequestParam(value = "experimentType") String experimentType,
            @RequestParam(value = "url") String url) {
        LOGGER.info("Testing {} for an {} experiment.", url, experimentType);
        NIFBasedAnnotatorWebservice annotator = new NIFBasedAnnotatorWebservice(url, "TEST",
                SingletonWikipediaApi.getInstance(), new DBPediaApi());
        JSONObject result = new JSONObject();
        experimentType = experimentType.toUpperCase();
        try {
            switch (ExperimentType.valueOf(experimentType)) {
            case A2KB: {
                annotator.solveA2W(NIF_WS_TEST_TEXT);
                break;
            }
            case C2KB: {
                annotator.solveA2W(NIF_WS_TEST_TEXT);
                break;
            }
            case D2KB: {
                annotator.solveA2W(NIF_WS_TEST_TEXT);
                break;
            }
            case Sa2KB: {
                annotator.solveA2W(NIF_WS_TEST_TEXT);
                break;
            }
            case Rc2KB: // falls through
            case Sc2KB: {
                annotator.solveA2W(NIF_WS_TEST_TEXT);
                break;
            }
            default: {
                throw new IllegalArgumentException("Got an unknown experiment type \"" + experimentType + "\".");
            }
            }
            result.put(RETURN_STATUS_NAME, true);
        } catch (Exception e) {
            LOGGER.error("Got an exception while testing {}. e = {}", url, e);
            result.put(RETURN_STATUS_NAME, false);
            result.put(RETURN_ERROR_MSG_NAME, e.getMessage());
        }
        return JSONValue.toJSONString(result);
    }
}
