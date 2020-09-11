package org.aksw.gerbil.web;

import org.aksw.gerbil.database.ExperimentDAO;
import org.apache.jena.atlas.json.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

@Controller
@PropertySource("gerbil.properties")
public class SubmissionController {


    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionController.class);
    private static final String SUCCESS_STRING = "Thanks for your submission!";
    private static final String FAIL_STRING = "Sorry. Something went wrong with your submission. Please try again or contact the web admins.";
    private static final String UPLOAD_FILE_FAIL = "Sorry. File could not be uploaded. Please try again later. Or contact the admins.";
    private static final String DATA_IS_NOT_VALID_FAIL = "Sorry the data is not valid, as one of the inputs are empty";


    @Value("${org.aksw.gerbil.SubmissionUploadPath}")
    private String path;

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;


    @RequestMapping("/submission")
    public ModelAndView submission(){
        ModelAndView model = new ModelAndView();
        model.setViewName("submission");
        return model;
    }

    public @ResponseBody
    @RequestMapping("/submit")
    String execute(@RequestParam(value = "submissionData") String submissionData) {
        LOGGER.debug("Got request on /submit(data={}", submissionData);
        Object obj = JSONValue.parse(submissionData);
        JSONObject submission = (JSONObject) obj;
        if(!verifyData(submission)){
           return DATA_IS_NOT_VALID_FAIL;
        }
        String zipFileName = submission.get("file").toString();
        //check if file exists
        if(!verifyZipFile(zipFileName)){
            return UPLOAD_FILE_FAIL;
        }
        //if file exists -> add record to db plus timestamp
        int success=-1;
        try {
            success = addRecord(submission);
        }catch(Exception e){
            LOGGER.error("Adding submission record thrown an error", e);
        }
        if(success>0){
            return SUCCESS_STRING;
        }
        else{
            return FAIL_STRING;
        }
    }

    private boolean verifyData(JSONObject submission) {
        String teamName = submission.get("teamName").toString();
        String email = submission.get("email").toString();
        String task = submission.get("task").toString();
        String zipFileName = submission.get("file").toString();
        if(teamName.isEmpty() || email.isEmpty() || task.isEmpty() || zipFileName.isEmpty()){
            return false;
        }
        return true;
    }

    private int addRecord(JSONObject submission) {
        String teamName = submission.get("teamName").toString();
        String email = submission.get("email").toString();
        String task = submission.get("task").toString();
        String zipFileName = submission.get("file").toString();
        return dao.insertSubmission(teamName, email, task, zipFileName);
    }

    private void deleteFile(String zipFileName) {
        File f = new File(path+File.separator+zipFileName);
        f.delete();
    }

    private boolean verifyZipFile(String zipFileName) {
        File f = new File(path+File.separator+zipFileName);
        return f.exists();
    }

}
