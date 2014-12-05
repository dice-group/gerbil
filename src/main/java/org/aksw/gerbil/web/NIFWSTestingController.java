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

@Controller
public class NIFWSTestingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NIFWSTestingController.class);

    private static final String RETURN_STATUS_NAME = "test_ok";
    // private static final String RETURN_STATUS_VALUE_OK = "ok";
    // private static final String RETURN_STATUS_VALUE_ERROR = "error";
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
