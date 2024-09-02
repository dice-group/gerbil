package org.aksw.gerbil.tools;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.io.nif.NIFWriter;
import org.aksw.gerbil.io.nif.impl.TurtleNIFWriter;
import org.aksw.gerbil.qa.datatypes.Property;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;

public class QA2C2KBConverter {

    public static void main(String[] args) {
        GerbilConfiguration.loadAdditionalProperties("src/main/properties/gerbil.properties");
        run();
    }

    public static void run() {
        Dataset dataset = loadDataset();
        if (dataset == null) {
            return;
        }
        for (Document document : dataset.getInstances()) {
            // Remove all property markings
            document.setMarkings(document.getMarkings(Meaning.class).stream().filter(m -> !Property.class.isInstance(m))
                    .collect(Collectors.toList()));
            document.setDocumentURI(document.getDocumentURI().replace('#', '-'));
        }
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream("qald10-test-nif.ttl"))) {
            NIFWriter writer = new TurtleNIFWriter();
            writer.writeNIF(dataset.getInstances(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Dataset loadDataset() {
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
        adapterManager.setDatasets(DatasetsConfig.datasets(null, null));
        DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig("QALD10 Test Multilingual",
                ExperimentType.QA, "en");
        try {
            return datasetConfig.getDataset(ExperimentType.QA);
        } catch (GerbilException e) {
            e.printStackTrace();
        }
        return null;
    }

}
