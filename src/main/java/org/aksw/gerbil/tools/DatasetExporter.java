package org.aksw.gerbil.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.io.nif.NIFWriter;
import org.aksw.gerbil.io.nif.impl.TurtleNIFWriter;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to export the datasets of this GERBIL instance as NIF
 * file. It can also preprocess the datasets to allow sameAs retrieval and
 * entity checking before exporting the updated datasets.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class DatasetExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetExporter.class);

    public static void main(String[] args) {
        // Input arguments should define 1) target directory and 2) experiment type
        if (args.length < 1) {
            LOGGER.error("Wrong usage. Correct usage: '<path-to-output-directory> <exp-type>'.");
            return;
        }
        File outDir = new File(args[0]);
        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                LOGGER.error("Error while creating output dir {}. Aborting.", outDir);
                return;
            }
        }
        ExperimentType expType = ExperimentType.valueOf(args[1]);

        EntityCheckerManager entityChecker = RootConfig.getEntityCheckerManager();
        SameAsRetriever sameAsRetriever = RootConfig.createSameAsRetriever();
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
        adapterManager.setDatasets(
                DatasetsConfig.datasets(entityChecker, sameAsRetriever));

        // Only export dataset if target file doesn't exist
        Set<String> datasetNames = adapterManager.getDatasetNamesForExperiment(expType);
        DatasetConfiguration config;
        Dataset dataset;
        int count = 0;
        int datasetCount = datasetNames.size();
        for (String datasetName : datasetNames) {
            ++count;
            LOGGER.info("Starting dataset {}/{}: {}...", count, datasetCount, datasetName);
            // Prepare dataset
            config = adapterManager.getDatasetConfig(datasetName, expType);
            try {
                dataset = config.getDataset(expType);
                // Export as NIF file to output directory
                printDataset(dataset, createOutputFile(outDir, datasetName));
                LOGGER.info("Finished dataset {}/{}: {}...", count, datasetCount, datasetName);
            } catch (Exception e) {
                LOGGER.error("Exception while handling dataset " + datasetName + ". The dataset will be ignored.", e);
            }
        }
    }

    private static void printDataset(Dataset dataset, File outputFile) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(outputFile)) {
            NIFWriter writer = new TurtleNIFWriter();
            writer.writeNIF(dataset.getInstances(), fout);
        }
    }

    private static File createOutputFile(File outDir, String datasetName) {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(outDir.getAbsolutePath());
        nameBuilder.append(File.separator);
        nameBuilder.append(datasetName.replace(" ", "-"));
        nameBuilder.append(".ttl");
        return new File(nameBuilder.toString());
    }
}
