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
package org.aksw.gerbil.execute;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.MetricsResultSet;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.problems.C2WDataset;
import it.acubelab.batframework.problems.C2WSystem;
import it.acubelab.batframework.problems.D2WDataset;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.problems.Sc2WSystem;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.Pair;
import it.acubelab.batframework.utils.RunExperiments;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.HashMap;
import java.util.Vector;

import org.aksw.gerbil.bat.annotator.ErrorCounter;
import org.aksw.gerbil.bat.annotator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.MatchingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentTaskExecuter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentTaskExecuter.class);

    private ExperimentDAO experimentDAO;
    private ExperimentTaskConfiguration configuration;
    private int experimentTaskId;
    private WikipediaApiInterface wikiAPI;

    public ExperimentTaskExecuter(int experimentTaskId, ExperimentDAO experimentDAO,
            ExperimentTaskConfiguration configuration, WikipediaApiInterface wikiAPI) {
        this.experimentDAO = experimentDAO;
        this.configuration = configuration;
        this.experimentTaskId = experimentTaskId;
        this.wikiAPI = wikiAPI;
    }

    @Override
    public void run() {
        LOGGER.info("Task started " + configuration.toString());
        try {
            // Create dataset
            TopicDataset dataset = configuration.datasetConfig.getDataset(configuration.type);
            if (dataset == null) {
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // Create annotator
            TopicSystem annotator = configuration.annotatorConfig.getAnnotator(configuration.type);
            annotator = ErrorCountingAnnotatorDecorator.createDecorator(annotator);
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // create matching
            MatchRelation<?> matching = MatchingFactory.createMatchRelation(wikiAPI, configuration.matching,
                    configuration.type);
            if (matching == null) {
                throw new GerbilException("matching=\"" + configuration.matching.name()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.MATCHING_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // perform experiment
            MetricsResultSet metrics = runExperiment(dataset, annotator, matching).second;

            int errorCount = 0;
            if (annotator instanceof ErrorCounter) {
                errorCount = ((ErrorCounter) annotator).getErrorCount();
            }
            // create result object
            double results[] = new double[6];
            results[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX] = metrics.getMacroF1();
            results[ExperimentTaskResult.MACRO_PRECISION_INDEX] = metrics.getMacroPrecision();
            results[ExperimentTaskResult.MACRO_RECALL_INDEX] = metrics.getMacroRecall();
            results[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX] = metrics.getMicroF1();
            results[ExperimentTaskResult.MICRO_PRECISION_INDEX] = metrics.getMicroPrecision();
            results[ExperimentTaskResult.MICRO_RECALL_INDEX] = metrics.getMicroRecall();
            ExperimentTaskResult result = new ExperimentTaskResult(configuration, results, ExperimentDAO.TASK_FINISHED,
                    errorCount);

            // store result
            experimentDAO.setExperimentTaskResult(experimentTaskId, result);
        } catch (GerbilException e) {
            LOGGER.error("Got an error while running the task. Storing the error code in the db...", e);
            // store error
            experimentDAO.setExperimentState(experimentTaskId, e.getErrorType().getErrorCode());
        } catch (Exception e) {
            LOGGER.error("Error while trying to execute experiment.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Pair<Float, MetricsResultSet> runExperiment(TopicDataset dataset, TopicSystem annotator,
            MatchRelation<?> matching)
            throws GerbilException {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results = null;
        switch (configuration.type) {
        case D2W: {
            Vector<D2WSystem> d2wAnnotator = new Vector<D2WSystem>(1);
            d2wAnnotator.add((D2WSystem) annotator);
            Vector<D2WDataset> d2wDataset = new Vector<D2WDataset>(1);
            d2wDataset.add((D2WDataset) dataset);
            try {
                results = RunExperiments.performD2WExpVarThreshold(d2wAnnotator, null, d2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case A2W: {
            Vector<A2WSystem> a2wAnnotator = new Vector<A2WSystem>(1);
            a2wAnnotator.add((A2WSystem) annotator);
            Vector<A2WDataset> a2wDataset = new Vector<A2WDataset>(1);
            a2wDataset.add((A2WDataset) dataset);
            Vector<MatchRelation<Annotation>> matchings = new Vector<MatchRelation<Annotation>>(1);
            matchings.add((MatchRelation<Annotation>) matching);
            try {
                results = RunExperiments.performA2WExpVarThreshold(matchings, a2wAnnotator, null, a2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case Sa2W: {
            Vector<Sa2WSystem> sa2wAnnotator = new Vector<Sa2WSystem>(1);
            sa2wAnnotator.add((Sa2WSystem) annotator);
            Vector<A2WDataset> a2wDataset = new Vector<A2WDataset>(1);
            a2wDataset.add((A2WDataset) dataset);
            Vector<MatchRelation<Annotation>> matchings = new Vector<MatchRelation<Annotation>>(1);
            matchings.add((MatchRelation<Annotation>) matching);
            try {
                results = RunExperiments.performA2WExpVarThreshold(matchings, null, sa2wAnnotator, a2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case C2W: {
            Vector<C2WSystem> c2wAnnotator = new Vector<C2WSystem>(1);
            c2wAnnotator.add((C2WSystem) annotator);
            Vector<C2WDataset> c2wDataset = new Vector<C2WDataset>(1);
            c2wDataset.add((C2WDataset) dataset);
            Vector<MatchRelation<Tag>> matchings = new Vector<MatchRelation<Tag>>(1);
            matchings.add((MatchRelation<Tag>) matching);
            try {
                results = RunExperiments.performC2WExpVarThreshold(matchings, null, null,
                        null, c2wAnnotator, c2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case Sc2W: // Falls through
        case Rc2W: {
            Vector<Sc2WSystem> rc2wAnnotator = new Vector<Sc2WSystem>(1);
            rc2wAnnotator.add((Sc2WSystem) annotator);
            Vector<C2WDataset> rc2wDataset = new Vector<C2WDataset>(1);
            rc2wDataset.add((C2WDataset) dataset);
            Vector<MatchRelation<Tag>> matchings = new Vector<MatchRelation<Tag>>(1);
            matchings.add((MatchRelation<Tag>) matching);
            try {
                results = RunExperiments.performC2WExpVarThreshold(matchings, null, null,
                        rc2wAnnotator, null, rc2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        default:
            throw new GerbilException("This experiment type isn't implemented yet. Sorry for this.",
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        return RunExperiments.getBestRecord(results, matching.getName(), annotator.getName(), dataset.getName());
    }
}
