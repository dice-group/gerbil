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
package org.aksw.gerbil.dataid;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.aksw.gerbil.database.ResultNameToIdMapping;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.semantic.vocabs.CUBE;
import org.aksw.gerbil.semantic.vocabs.GERBIL;
import org.aksw.gerbil.web.ExperimentTaskStateHelper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import com.carrotsearch.hppc.IntDoubleOpenHashMap;

public class DataIDGenerator {

    private static final String EXPERIMENT_PREFIX = "experiment?id=";
    private static final String EXPERIMENT_TASK_PREFIX = "experimentTask_";
    private static final String DATASET_DATAID = "dataId/corpora/";
    private static final String ANNOTATOR_DATAID = "dataId/annotators/";
    private static final String DATAID_EXTENSION = "";

    private String gerbilURL;

    public DataIDGenerator(String gerbilURL) {
        this.gerbilURL = gerbilURL;
    }

    public Model generateDataIDModel() {
        // create an empty JENA Model
        Model model = ModelFactory.createDefaultModel();

        // setting namespaces
        model.setNsPrefix("gerbil", GERBIL.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("qb", CUBE.getURI());

        return model;
    }

    public String createDataIDModel(List<ExperimentTaskResult> results, String eID) {
        // If the experiment is not existing (== there are no results), return
        // an empty String
        if (results.size() == 0) {
            return "";
        }

        Model model = generateDataIDModel();

        addToModel(model, results, eID);

        // writing dataid result to output
        OutputStream o = new ByteArrayOutputStream();

        // creating json-ld output format
        RDFDataMgr.write(o, model, Lang.JSONLD);

        return o.toString();
    }

    public void addToModel(Model model, List<ExperimentTaskResult> results, String eID) {
        if (results.size() == 0) {
            return;
        }

        Resource experiment = createExperimentResource(model, eID);

        boolean first = true;
        Iterator<ExperimentTaskResult> resultIterator = results.iterator();
        ExperimentTaskResult result;
        // iterating over the experiments
        while (resultIterator.hasNext()) {
            result = resultIterator.next();
            // If this is the first experiment result, use it to get further
            // properties of the experiment (matching, ...)
            if (first) {
                Resource r = GERBIL.getExperimentTypeResource(result.type);
                if (r != null) {
                    experiment.addProperty(GERBIL.experimentType, r);
                }
                r = GERBIL.getMatchingResource(result.matching);
                if (r != null) {
                    experiment.addProperty(GERBIL.matching, r);
                }
                first = false;
            }
            // create experiment task
            addExperimentTask(model, result, experiment);
        }
    }

    public Resource createExperimentResource(Model model, String eID) {
        // create experiment resource
        Resource experiment = model.createResource(gerbilURL + EXPERIMENT_PREFIX + eID);
        experiment.addProperty(RDF.type, CUBE.Dataset);
        experiment.addProperty(RDF.type, GERBIL.Experiment);

        model.add(experiment, RDFS.label, "Experiment " + eID);
        model.add(experiment, CUBE.structure, GERBIL.DSD);

        return experiment;
    }

    public void addExperimentTask(Model model, ExperimentTaskResult result, Resource experiment) {
        addExperimentTask(model, result, experiment, null);
    }

    public void addExperimentTask(Model model, ExperimentTaskResult result, Resource experiment,
            Resource superExpTask) {
        List<Resource> experimentTasks = new ArrayList<Resource>();
        createExperimentTask(model, result, superExpTask, experimentTasks);
        linkTasksToExperiment(model, experiment, experimentTasks);
    }

    public void linkTasksToExperiment(Model model, Resource experiment, List<Resource> experimentTasks) {
        for (Resource experimentTask : experimentTasks) {
            model.add(experimentTask, CUBE.dataset, experiment.getURI());
        }
    }

    public void createExperimentTask(Model model, ExperimentTaskResult result, Resource superExpTask,
            List<Resource> experimentTasks) {
        // create Resource
        Resource experimentTask = model.createResource(generateExperimentTaskUri(result.idInDb));
        experimentTasks.add(experimentTask);
        if (model.containsResource(experimentTask)) {
            return;
        }
        experimentTask.addProperty(RDF.type, CUBE.Observation);

        // add annotator and dataset
        experimentTask.addProperty(GERBIL.annotator,
                gerbilURL + ANNOTATOR_DATAID + DataIDUtils.treatsNames(result.dataset) + DATAID_EXTENSION);
        experimentTask.addProperty(GERBIL.dataset,
                gerbilURL + DATASET_DATAID + DataIDUtils.treatsNames(result.annotator) + DATAID_EXTENSION);

        // set the status of this task
        model.add(experimentTask, GERBIL.statusCode, model.createTypedLiteral(result.state));

        if (superExpTask != null) {
            model.add(experimentTask, GERBIL.subExperimentOf, superExpTask);
        }

        // If this task has been finished
        if (ExperimentTaskStateHelper.taskFinished(result)) {
            // creating and setting literals for the current experiment
            model.add(experimentTask, GERBIL.microF1,
                    model.createTypedLiteral(String.valueOf(result.getMicroF1Measure()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.microPrecision,
                    model.createTypedLiteral(String.valueOf(result.getMicroPrecision()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.microRecall,
                    model.createTypedLiteral(String.valueOf(result.getMicroRecall()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroF1,
                    model.createTypedLiteral(String.valueOf(result.getMacroF1Measure()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroPrecision,
                    model.createTypedLiteral(String.valueOf(result.getMacroPrecision()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroRecall,
                    model.createTypedLiteral(String.valueOf(result.getMacroRecall()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.errorCount, model.createTypedLiteral(String.valueOf(result.errorCount)));

            if (result.hasAdditionalResults()) {
                IntDoubleOpenHashMap additionalResults = result.getAdditionalResults();
                String propertyUri;
                ResultNameToIdMapping mapping = ResultNameToIdMapping.getInstance();
                for (int i = 0; i < additionalResults.allocated.length; ++i) {
                    if (additionalResults.allocated[i]) {
                        propertyUri = mapping.getResultName(additionalResults.keys[i]);
                        if (propertyUri != null) {
                            propertyUri = GERBIL.getURI() + propertyUri.replace(" ", "_");
                            model.add(experimentTask, model.createProperty(propertyUri), model.createTypedLiteral(
                                    String.valueOf(additionalResults.values[i]), XSDDatatype.XSDdecimal));
                        }
                    }
                }
            }
            if (result.hasSubTasks()) {
                for (ExperimentTaskResult subResult : result.getSubTasks()) {
                    createExperimentTask(model, subResult, experimentTask, experimentTasks);
                }
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(result.timestamp);
        model.add(experimentTask, GERBIL.timestamp, model.createTypedLiteral(cal));
    }

    public String generateExperimentTaskUri(int taskId) {
        return gerbilURL + EXPERIMENT_TASK_PREFIX + taskId;
    }

}
