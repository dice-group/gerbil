package org.aksw.gerbil.web;

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

    @SuppressWarnings("unchecked")
    @RequestMapping("/testNifWs")
    public @ResponseBody
    String testWebService(@RequestParam(value = "experimentType") String experimentType,
            @RequestParam(value = "url") String url) {
        LOGGER.info("Testing {} for an {} experiment.", url, experimentType);
        NIFBasedAnnotatorWebservice annotator = new NIFBasedAnnotatorWebservice(url, "TEST");
        JSONObject result = new JSONObject();
        experimentType = experimentType.toUpperCase();
        Document document = new DocumentImpl();
        document.setText(NIF_WS_TEST_TEXT);
        try {
            switch (ExperimentType.valueOf(experimentType)) {
            case Rc2KB: // falls through
            case Sc2KB:
            case C2KB: {
                // annotator.solveA2W(NIF_WS_TEST_TEXT);
                // FIXME
                break;
            }
            case EntityLinking:
            case D2KB: {
                annotator.performLinking(document);
                break;
            }
            case EntityExtraction:
            case Sa2KB:
            case A2KB: {
                annotator.performExtraction(document);
                break;
            }
            case EntityRecognition: {
                annotator.performRecognition(document);
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
