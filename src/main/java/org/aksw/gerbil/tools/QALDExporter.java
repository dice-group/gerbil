package org.aksw.gerbil.tools;

import java.io.FileWriter;
import java.io.Writer;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class QALDExporter {

    public static final String QALD10_BASE_IRI = "https://github.com/qald_10#";

    public static void main(String[] args) {
        GerbilConfiguration.loadAdditionalProperties("src/main/properties/gerbil.properties");
        run();
    }

    public static void run() {
        Dataset dataset = loadDataset();
        if(dataset == null) {
            return;
        }
        Model model = ModelFactory.createDefaultModel();
        for (Document document : dataset.getInstances()) {
            addQuestion(model, document);
        }
        model.setNsPrefix("qa", QALD10_BASE_IRI);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        try (Writer writer = new FileWriter("qald10-simple.nt")) {
            model.write(writer, "nt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Dataset loadDataset() {
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
        adapterManager.setDatasets(DatasetsConfig.datasets(null, null));
        DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig("QALD10 Test Multilingual", ExperimentType.QA, "en");
        try {
            return datasetConfig.getDataset(ExperimentType.QA);
        } catch (GerbilException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addQuestion(Model model, Document document) {
        Resource questionClass = model.getResource("http://quans-namespace.org/#QUESTION");
        
        Resource questionResource = model.createResource(QALD10_BASE_IRI + "QUE" + document.getDocumentURI().substring("http://qa.gerbil.aksw.org/QALD10+Test+Multilingual/question#".length()));
        model.add(questionResource, RDF.type, questionClass);
        model.add(questionResource, RDF.value, document.getText());
        model.add(questionResource, RDFS.comment, document.getText());
    }
}
