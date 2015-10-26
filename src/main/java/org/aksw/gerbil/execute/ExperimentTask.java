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
package org.aksw.gerbil.execute;


import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.metrics.MatchRelation;
import it.unipi.di.acube.batframework.metrics.MetricsResultSet;
import it.unipi.di.acube.batframework.problems.*;
import it.unipi.di.acube.batframework.utils.Pair;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.bat.annotator.ErrorCounter;
import org.aksw.gerbil.bat.annotator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.bat.utils.RunExperiments;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentTaskState;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.MatchingFactory;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Vector;

public class ExperimentTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentTask.class);

    private ExperimentDAO experimentDAO;
    private ExperimentTaskConfiguration configuration;
    private int experimentTaskId;
    private WikipediaApiInterface wikiAPI;
    private ExperimentTaskState taskState = null;

    public ExperimentTask(int experimentTaskId, ExperimentDAO experimentDAO,
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
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName() + "\" experimentType=\""
                        + configuration.type.name() + "\".", ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // Create annotator
            TopicSystem annotator = configuration.annotatorConfig.getAnnotator(configuration.type);
            // TODO add time measuring
            // annotator =
            // TimeMeasuringAnnotatorDecorator.createDecorator(annotator);
            annotator = ErrorCountingAnnotatorDecorator.createDecorator(annotator, dataset.getSize());
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // create matching
            MatchRelation<?> matching = MatchingFactory.createMatchRelation(wikiAPI, configuration.matching,
                    configuration.type);
            if (matching == null) {
                throw new GerbilException("matching=\"" + configuration.matching.name() + "\" experimentType=\""
                        + configuration.type.name() + "\".", ErrorTypes.MATCHING_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            taskState = new ExperimentTaskState(dataset.getSize());
            // perform experiment
            MetricsResultSet metrics = runExperiment(dataset, annotator, matching, taskState).second;

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
            LOGGER.info("Task Finished " + configuration.toString());
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
            MatchRelation<?> matching, ExperimentTaskState state) throws GerbilException {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results = null;
        switch (configuration.type) {
        case D2KB: {
            Vector<D2WSystem> d2wAnnotator = new Vector<D2WSystem>(1);
            d2wAnnotator.add((D2WSystem) annotator);
            Vector<D2WDataset> d2wDataset = new Vector<D2WDataset>(1);
            d2wDataset.add((D2WDataset) dataset);
            try {
                results = RunExperiments.performD2WExpVarThreshold(d2wAnnotator, null, d2wDataset, state, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case A2KB: {
            Vector<A2WSystem> a2wAnnotator = new Vector<A2WSystem>(1);
            a2wAnnotator.add((A2WSystem) annotator);
            Vector<A2WDataset> a2wDataset = new Vector<A2WDataset>(1);
            a2wDataset.add((A2WDataset) dataset);
            Vector<MatchRelation<Annotation>> matchings = new Vector<MatchRelation<Annotation>>(1);
            matchings.add((MatchRelation<Annotation>) matching);
            try {
                results = RunExperiments.performA2WExpVarThreshold(matchings, a2wAnnotator, null, a2wDataset, state,
                        wikiAPI);
                // LOGGER.info("average time needed by {} on {}: {}",
                // annotator.getName(), dataset.getName(),
                // BenchmarkCache.getAvgA2WTimingsForDataset(annotator.getName(),
                // dataset.getName()));
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case Sa2KB: {
            Vector<Sa2WSystem> sa2wAnnotator = new Vector<Sa2WSystem>(1);
            sa2wAnnotator.add((Sa2WSystem) annotator);
            Vector<A2WDataset> a2wDataset = new Vector<A2WDataset>(1);
            a2wDataset.add((A2WDataset) dataset);
            Vector<MatchRelation<Annotation>> matchings = new Vector<MatchRelation<Annotation>>(1);
            matchings.add((MatchRelation<Annotation>) matching);
            try {
                results = RunExperiments.performA2WExpVarThreshold(matchings, null, sa2wAnnotator, a2wDataset, state,
                        wikiAPI);
                // LOGGER.info("average time needed by {} on {}: {}",
                // annotator.getName(), dataset.getName(),
                // BenchmarkCache.getAvgSa2WTimingsForDataset(annotator.getName(),
                // dataset.getName()));
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case C2KB: {
            Vector<C2WSystem> c2wAnnotator = new Vector<C2WSystem>(1);
            c2wAnnotator.add((C2WSystem) annotator);
            Vector<C2WDataset> c2wDataset = new Vector<C2WDataset>(1);
            c2wDataset.add((C2WDataset) dataset);
            Vector<MatchRelation<Tag>> matchings = new Vector<MatchRelation<Tag>>(1);
            matchings.add((MatchRelation<Tag>) matching);
            try {
                results = RunExperiments.performC2WExpVarThreshold(matchings, null, null, null, c2wAnnotator,
                        c2wDataset, state, wikiAPI);
                // LOGGER.info("average time needed by {} on {}: {}",
                // annotator.getName(), dataset.getName(),
                // BenchmarkCache.getAvgC2WTimingsForDataset(annotator.getName(),
                // dataset.getName()));
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case Sc2KB: // Falls through
        case Rc2KB: {
            Vector<Sc2WSystem> rc2wAnnotator = new Vector<Sc2WSystem>(1);
            rc2wAnnotator.add((Sc2WSystem) annotator);
            Vector<C2WDataset> rc2wDataset = new Vector<C2WDataset>(1);
            rc2wDataset.add((C2WDataset) dataset);
            Vector<MatchRelation<Tag>> matchings = new Vector<MatchRelation<Tag>>(1);
            matchings.add((MatchRelation<Tag>) matching);
            try {
                results = RunExperiments.performC2WExpVarThreshold(matchings, null, null, rc2wAnnotator, null,
                        rc2wDataset, state, wikiAPI);
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

    @Override
    public String getId() {
        return configuration.toString();
    }

    @Override
    public String getProgress() {
        if (taskState != null) {
            return (taskState.getExperimentTaskProcess() * 100.0) + "% of dataset";
        } else {
            return null;
        }
    }

}
