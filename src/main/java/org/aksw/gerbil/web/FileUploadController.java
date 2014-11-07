package org.aksw.gerbil.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.aksw.gerbil.transfer.FileContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/file")
public class FileUploadController {

    private static final transient Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private String path="/data/d.cherix/tmp/";
    
    public FileUploadController(){
        
    }

    public FileUploadController(String path) {
        this.path = path;
    }
    
    @RequestMapping(value="upload", method = RequestMethod.GET)
    public ModelAndView upload(){
        return new ModelAndView("fileupload");
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public @ResponseBody LinkedList<FileContainer> upload(MultipartHttpServletRequest request,
            HttpServletResponse response) {
        LinkedList<FileContainer> files = new LinkedList<FileContainer>();
        MultipartFile mpf = null;

        for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
            mpf = request.getFile(it.next());
            logger.debug("{} uploaded", mpf.getOriginalFilename());

            FileContainer fileContainer = new FileContainer();
            fileContainer.setName(mpf.getOriginalFilename());
            fileContainer.setSize(mpf.getSize() / 1024 + "Kb");
            fileContainer.setFileType(mpf.getContentType());

            try {
                fileContainer.setBytes(mpf.getBytes());
                FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(path + mpf.getOriginalFilename()));

            } catch (IOException e) {
                logger.error("Error during file upload", e);
                fileContainer.setError(e.getMessage());
            }
            files.add(fileContainer);

        }
        return files;
    }

}
