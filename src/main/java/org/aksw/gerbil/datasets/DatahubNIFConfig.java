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
package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.aksw.gerbil.bat.datasets.FileBasedNIFDataset;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class DatahubNIFConfig extends AbstractDatasetConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DatahubNIFConfig.class);
    private static final String DATAHUB_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.DatahubNIFConfig.datasetFiles";

    private WikipediaApiInterface wikiApi;
    private String datasetUrl;
    private RestTemplate rt;

    public DatahubNIFConfig(WikipediaApiInterface wikiApi, String datasetName, String datasetUrl, boolean couldBeCached) {
        super(datasetName, couldBeCached, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
        this.datasetUrl = datasetUrl;
        rt = new RestTemplate();
    }

    /**
     * We have to synchronize this method. Otherwise every experiment thread would check the file and try to download
     * the data or try to use the file even the download hasn't been completed.
     */
    @Override
    protected synchronized TopicDataset loadDataset() throws Exception {
        String nifFile = GerbilConfiguration.getInstance().getString(DATAHUB_DATASET_FILE_PROPERTY_NAME) + getName();
        logger.debug("FILE {}", nifFile);
        File f = new File(nifFile);
        if (!f.exists()) {
            logger.debug("file {} does not exist. need to download", nifFile);
            String data = rt.getForObject(datasetUrl, String.class);
            Path path = Paths.get(nifFile);
            Files.createDirectories(path.getParent());
            Path file = Files.createFile(path);
            Files.write(file, data.getBytes(), StandardOpenOption.WRITE);
        }
        FileBasedNIFDataset dataset = new FileBasedNIFDataset(wikiApi, nifFile, getName(), Lang.TTL);
        dataset.init();
        return dataset;
    }
}
