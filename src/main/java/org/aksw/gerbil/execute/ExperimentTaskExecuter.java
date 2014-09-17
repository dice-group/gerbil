package org.aksw.gerbil.execute;

import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.metrics.MetricsResultSet;
import it.acubelab.batframework.problems.D2WDataset;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.Pair;
import it.acubelab.batframework.utils.RunExperiments;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

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
        try {
            // Create dataset
            TopicDataset dataset = configuration.datasetConfig.getDataset(configuration.type);
            if (dataset == null) {
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getDatasetName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // Create annotator
            TopicSystem annotator = configuration.annotatorConfig.getAnnotator(configuration.type);
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getAnnotatorName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // create matching
            MatchRelation<?> matching = MatchingFactory.createMatchRelation(configuration.matching);
            if (matching == null) {
                throw new GerbilException("matching=\"" + configuration.matching.name()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.MATCHING_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // perform experiment
            MetricsResultSet metrics = runExperiment(dataset, annotator, matching).second;

            // create result object
            double results[] = new double[6];
            results[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX] = metrics.getMacroF1();
            results[ExperimentTaskResult.MACRO_PRECISION_INDEX] = metrics.getMacroPrecision();
            results[ExperimentTaskResult.MACRO_RECALL_INDEX] = metrics.getMacroRecall();
            results[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX] = metrics.getMicroF1();
            results[ExperimentTaskResult.MICRO_PRECISION_INDEX] = metrics.getMicroPrecision();
            results[ExperimentTaskResult.MICRO_RECALL_INDEX] = metrics.getMicroRecall();
            ExperimentTaskResult result = new ExperimentTaskResult(configuration, results);

            // store result
            experimentDAO.setExperimentTaskResult(experimentTaskId, result);
        } catch (GerbilException e) {
            double results[] = new double[6];
            Arrays.fill(results, e.getErrorType().getErrorCode());
            // store error
            experimentDAO.setExperimentTaskResult(experimentTaskId, new ExperimentTaskResult(configuration, results));
        } catch (Exception e) {
            LOGGER.error("Error while trying to execute experiment.", e);
        }
    }

    private Pair<Float, MetricsResultSet> runExperiment(TopicDataset dataset, TopicSystem annotator,
            MatchRelation<?> matching)
            throws GerbilException {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Float, MetricsResultSet>>>> results = null;
        switch (configuration.type) {
        case D2W: {
            Vector<D2WSystem> d2wAnnotator = new Vector<D2WSystem>();
            d2wAnnotator.add((D2WSystem) annotator);
            Vector<D2WDataset> d2wDataset = new Vector<D2WDataset>();
            d2wDataset.add((D2WDataset) dataset);
            try {
                results = RunExperiments.performD2WExpVarThreshold(d2wAnnotator, null, d2wDataset, wikiAPI);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        default:
            break;
        }
        return RunExperiments.getBestRecord(results, matching.getName(), annotator.getName(), dataset.getName());
    }
}
