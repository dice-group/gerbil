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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class DataIDGenerator {

    private static final String EXPERIMENT_PREFIX = "#experiment_";
    private static final String DATASET_DATAID = "dataId/corpora/";
    private static final String ANNOTATOR_DATAID = "dataId/annotators/";
    private static final String DATAID_EXTENSION = "";

    private String gerbilURL;
    private String gerbilFullURL;

    public DataIDGenerator(String gerbilURL, String gerbilFullURL) {
        this.gerbilURL = gerbilURL;
        this.gerbilFullURL = gerbilFullURL;
    }

    public String createDataIDModel(List<ExperimentTaskResult> results, String eID) {

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
                Resource r = GERBIL.getExperimentTypeResource(result.type);
                if (r != null) {
                    experiment.addProperty(GERBIL.experimentType, r);
                }
                r = GERBIL.getMatchingResource(result.matching);
                if (r != null) {
                    experiment.addProperty(GERBIL.matching, r);
                }
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
        Resource experiment = model.createResource(gerbilFullURL + EXPERIMENT_PREFIX + eID);
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
        experimentTask.addProperty(GERBIL.annotator,
                gerbilURL + DATASET_DATAID + DataIDUtils.treatsNames(result.annotator) + DATAID_EXTENSION);
        experimentTask.addProperty(GERBIL.dataset,
                gerbilURL + ANNOTATOR_DATAID + DataIDUtils.treatsNames(result.dataset) + DATAID_EXTENSION);

        // set the status of this task
        model.add(experimentTask, GERBIL.statusCode, model.createTypedLiteral(result.state));

        // If this task has been finished
        if (ExperimentTaskStateHelper.taskFinished(result)) {
            // creating and setting literals for the current experiment
            model.add(experimentTask, GERBIL.microF1, model.createTypedLiteral(result.getMicroF1Measure()));
            model.add(experimentTask, GERBIL.microPrecision,
                    model.createTypedLiteral(result.getMicroPrecision(), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.microRecall,
                    model.createTypedLiteral(result.getMicroRecall(), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroF1,
                    model.createTypedLiteral(result.getMacroF1Measure(), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroPrecision,
                    model.createTypedLiteral(result.getMacroPrecision(), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.macroRecall,
                    model.createTypedLiteral(String.valueOf(result.getMacroRecall()), XSDDatatype.XSDdecimal));
            model.add(experimentTask, GERBIL.errorCount, model.createTypedLiteral(result.errorCount));
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(result.timestamp);
        model.add(experimentTask, GERBIL.timestamp, model.createTypedLiteral(cal));
    }

}
