package org.aksw.gerbil.tools;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.EntityCheckerManagerImpl;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.ErrorFixingSameAsRetriever;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.web.config.AdapterList;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriExport {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriExport.class);

    private static final SameAsRetriever SAME_AS_RETRIEVER = new ErrorFixingSameAsRetriever();
    private static final EntityCheckerManager ENTITY_CHECKER_MANAGER = new EntityCheckerManagerImpl();

    public static void main(String[] args) {
        PrintStream pout = null;
        try {
            pout = new PrintStream(new BufferedOutputStream(new FileOutputStream("exportedURIs.txt")));

            AdapterList<DatasetConfiguration> adapterList = DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER,
                    SAME_AS_RETRIEVER);
            List<DatasetConfiguration> datasetConfigs = null;
            datasetConfigs = adapterList.getAdaptersForExperiment(ExperimentType.D2KB);
            for (DatasetConfiguration datasetConfig : datasetConfigs) {
                try {
                    Dataset dataset = datasetConfig.getDataset(ExperimentType.D2KB);
                    printDatasetUris(dataset, pout);
                    LOGGER.info("Finished {}", dataset.getName());
                } catch (GerbilException e) {
                    LOGGER.error("Couldn't load dataset. It will be ignored.", e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while writing file. Aborting.", e);
        } finally {
            IOUtils.closeQuietly(pout);
        }
    }

    private static void printDatasetUris(Dataset dataset, PrintStream pout) {
        for (Document document : dataset.getInstances()) {
            String text = document.getText();
            for (MeaningSpan meaning : document.getMarkings(MeaningSpan.class)) {
                for (String uri : meaning.getUris()) {
                    pout.print(uri);
                    pout.print('\t');
                    pout.print(text.substring(meaning.getStartPosition(),
                            meaning.getStartPosition() + meaning.getLength()));
                    pout.println();
                }
            }
        }
    }
}
