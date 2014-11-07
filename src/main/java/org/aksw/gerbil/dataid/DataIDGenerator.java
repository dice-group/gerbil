package org.aksw.gerbil.dataid;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.aksw.gerbil.dataid.vocabs.CUBE;
import org.aksw.gerbil.dataid.vocabs.GERBIL;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.web.ExperimentTaskStateHelper;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class DataIDGenerator {

    private static final String EXPERIMENT_PREFIX = "experiment_";
    private static final String DATASET_DATAID = "dataId/corpora/";
    private static final String ANNOTATOR_DATAID = "dataId/annotators/";
    private static final String DATAID_FILE = "/dataid.ttl";

    private String gerbilURL;

    public DataIDGenerator(String gerbilURL) {
        this.gerbilURL = gerbilURL;
    }

    public String createDataIDModel(List<ExperimentTaskResult> results, String eID) {

        // test with dataset 201411060000

        // create an empty JENA Model
        Model model = ModelFactory.createDefaultModel();

        // setting namespaces
        model.setNsPrefix("gerbil", GERBIL.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("qb", CUBE.getURI());

        // If the experiment is not existing (== there are no results), return
        // an empty String
        if (results.size() == 0) {
            return "";
        }

        Resource experiment = createExperimentResource(model, eID);

        int experimentNumber = 0;
        Iterator<ExperimentTaskResult> resultIterator = results.iterator();
        ExperimentTaskResult result;
        // iterating over the experiments
        while (resultIterator.hasNext()) {
            result = resultIterator.next();
            // If this is the first experiment result, use it to get further
            // properties of the experiment (matching, ...)
            if (experimentNumber == 0) {
                experiment.addProperty(GERBIL.experimentType, GERBIL.getExperimentTypeResource(result.type));
                experiment.addProperty(GERBIL.matching, GERBIL.getMatchingResource(result.matching));
            }
            // create experiment task
            addExperimentTask(model, result, experiment, experimentNumber);

            ++experimentNumber;
        }

        // writing dataid result to output (this should be removed)
        // RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);

        OutputStream o = new ByteArrayOutputStream();

        // creating json-ld output format
        RDFDataMgr.write(o, model, RDFFormat.JSONLD);

        return o.toString();
    }

    private Resource createExperimentResource(Model model, String eID) {
        // create experiment resource
        Resource experiment = model.createResource(gerbilURL + EXPERIMENT_PREFIX + eID);
        experiment.addProperty(RDF.type, CUBE.Dataset);
        experiment.addProperty(RDF.type, GERBIL.Experiment);

        model.add(experiment, RDFS.label, "Experiment " + eID);
        model.add(experiment, CUBE.structure, GERBIL.DSD);

        return experiment;
    }

    private void addExperimentTask(Model model, ExperimentTaskResult result, Resource experiment, int experimentNumber) {
        // create Resource
        Resource experimentTask = model.createResource(experiment.getURI() + "_task_" + experimentNumber);
        experimentTask.addProperty(RDF.type, CUBE.Observation);

        // add annotator and dataset
        experimentTask.addProperty(GERBIL.annotator, gerbilURL + DATASET_DATAID + result.annotator + DATAID_FILE);
        experimentTask.addProperty(GERBIL.dataset, gerbilURL + ANNOTATOR_DATAID + result.dataset + DATAID_FILE);

        // set the status of this task
        model.add(experimentTask, GERBIL.statusCode, model.createTypedLiteral(result.state));

        // If this task has been finished
        if (ExperimentTaskStateHelper.taskFinished(result)) {
            // creating and setting literals for the current experiment
            model.add(experimentTask, GERBIL.microF1, model.createTypedLiteral(result.getMicroF1Measure()));
            model.add(experimentTask, GERBIL.microPrecision, model.createTypedLiteral(result.getMicroPrecision()));
            model.add(experimentTask, GERBIL.microRecall, model.createTypedLiteral(result.getMicroRecall()));
            model.add(experimentTask, GERBIL.macroF1, model.createTypedLiteral(result.getMacroF1Measure()));
            model.add(experimentTask, GERBIL.macroPrecision, model.createTypedLiteral(result.getMacroPrecision()));
            model.add(experimentTask, GERBIL.macroRecall, model.createTypedLiteral(result.getMacroRecall()));
            model.add(experimentTask, GERBIL.errorCount, model.createTypedLiteral(result.errorCount));
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(result.timestamp);
        model.add(experimentTask, GERBIL.timestamp, model.createTypedLiteral(cal));
    }
}
