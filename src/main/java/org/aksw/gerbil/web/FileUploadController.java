/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.aksw.gerbil.transfer.FileMeta;
import org.aksw.gerbil.transfer.UploadFileContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/file")
@PropertySource("gerbil.properties")
public class FileUploadController {

    private static final transient Logger logger = LoggerFactory
            .getLogger(FileUploadController.class);
    @Value("${org.aksw.gerbil.UploadPath}")
    private String path;

    public FileUploadController() {
    }

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public ModelAndView upload() {
        return new ModelAndView("fileupload");
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadFileContainer> upload(
            MultipartHttpServletRequest request, HttpServletResponse response) {

        if (path == null) {
            logger.error("Path must be not null");
            return new ResponseEntity<UploadFileContainer>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LinkedList<FileMeta> files = new LinkedList<FileMeta>();
        MultipartFile mpf = null;

        for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
            mpf = request.getFile(it.next());
            logger.debug("{} uploaded", mpf.getOriginalFilename());

            FileMeta fileContainer = new FileMeta();
            fileContainer.setName(mpf.getOriginalFilename());
            fileContainer.setSize(mpf.getSize() / 1024 + "Kb");
            fileContainer.setFileType(mpf.getContentType());

            try {
                fileContainer.setBytes(mpf.getBytes());
                createFolderIfNotExists();
                FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(path
                        + mpf.getOriginalFilename()));

            } catch (IOException e) {
                logger.error("Error during file upload", e);
                fileContainer.setError(e.getMessage());
            }
            files.add(fileContainer);
        }

        UploadFileContainer uploadFileContainer = new UploadFileContainer(files);
        return new ResponseEntity<UploadFileContainer>(uploadFileContainer,
                HttpStatus.OK);
    }

    private void createFolderIfNotExists() {
        // create the upload folder if it does not exist
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

}
