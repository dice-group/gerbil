package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
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
    private static final String NIF_DATASET_FILE_PROPERTY_NAME = "org.aksw.gerbil.datasets.Datahub";
    private DBPediaApi dbpediaApi;
    private WikipediaApiInterface wikiApi;
    private String datasetUrl;

    private RestTemplate rt;

    public DatahubNIFConfig(WikipediaApiInterface wikiApi, DBPediaApi dbpediaApi, String datasetName,
            String datasetUrl, boolean couldBeCached) {
        super(datasetName, couldBeCached, ExperimentType.Sa2W);
        this.wikiApi = wikiApi;
        this.dbpediaApi = dbpediaApi;
        this.datasetUrl = datasetUrl;
        rt = new RestTemplate();
    }

    @Override
    protected TopicDataset loadDataset() throws Exception {
        String nifFile = GerbilConfiguration.getInstance().getString(NIF_DATASET_FILE_PROPERTY_NAME) + datasetName;
        logger.debug("FILE {}", nifFile);
        File f = new File(nifFile);
        if (!f.exists()) {
            logger.debug("file {} does not exists. need to download", nifFile);
            String data = rt.getForObject(datasetUrl, String.class);

            Path file = Files.createFile(Paths.get(nifFile));
            Files.write(file, data.getBytes(), StandardOpenOption.WRITE);
        }

        return new FileBasedNIFDataset(wikiApi, dbpediaApi, nifFile, getDatasetName(), Lang.TTL);
    }
}
