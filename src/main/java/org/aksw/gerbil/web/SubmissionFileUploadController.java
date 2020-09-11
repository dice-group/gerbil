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

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

@Controller
@RequestMapping("/submissionfile")
@PropertySource("gerbil.properties")
public class SubmissionFileUploadController {

    private static final transient Logger logger = LoggerFactory.getLogger(SubmissionFileUploadController.class);
    @Value("${org.aksw.gerbil.SubmissionUploadPath}")
    private String path;

    public SubmissionFileUploadController() {
    }

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public ModelAndView upload() {
        return new ModelAndView("submissionupload");
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<UploadFileContainer> upload(MultipartHttpServletRequest request,
            HttpServletResponse response) {

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
            UUID uuid = UUID.randomUUID();
            fileContainer.setName(uuid.toString()+"_"+mpf.getOriginalFilename());
            fileContainer.setSize(mpf.getSize() / 1024 + "Kb");
            fileContainer.setFileType(mpf.getContentType());

            try {
                fileContainer.setBytes(mpf.getBytes());
                createFolderIfNotExists();
                FileCopyUtils.copy(mpf.getBytes(),
                        new BufferedOutputStream(new FileOutputStream(path + uuid.toString()+"_"+mpf.getOriginalFilename())));
                // the copy method closed the output stream
            } catch (IOException e) {
                logger.error("Error during file upload", e);
                fileContainer.setError(e.getMessage());
            }
            files.add(fileContainer);
        }

        UploadFileContainer uploadFileContainer = new UploadFileContainer(files);
        return new ResponseEntity<UploadFileContainer>(uploadFileContainer, HttpStatus.OK);
    }

    private void createFolderIfNotExists() {
        // create the upload folder if it does not exist
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

}
