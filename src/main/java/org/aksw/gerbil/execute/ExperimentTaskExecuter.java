package org.aksw.gerbil.execute;

import it.acubelab.batframework.metrics.MatchRelation;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.problems.TopicSystem;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.MatchingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentTaskExecuter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentTaskExecuter.class);

    private ExperimentDAO experimentDAO;
    private ExperimentTaskConfiguration configuration;
    private int experimentTaskId;

    public ExperimentTaskExecuter(int experimentTaskId, ExperimentDAO experimentDAO,
            ExperimentTaskConfiguration configuration) {
        this.experimentDAO = experimentDAO;
        this.configuration = configuration;
        this.experimentTaskId = experimentTaskId;
    }

    @Override
    public void run() {
        try {
            // TODO create dataset
            TopicDataset dataset = configuration.datasetConfig.getDataset(configuration.type);
            if (dataset == null) {
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getDatasetName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // TODO create annotator
            TopicSystem annotator = configuration.annotatorConfig.getAnnotator(configuration.type);
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getAnnotatorName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // TODO create matching
            MatchRelation<?> matching = MatchingFactory.createMatchRelation(configuration.matching);
            if (matching == null) {
                throw new GerbilException("matching=\"" + configuration.matching.name()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.MATCHING_DOES_NOT_SUPPORT_EXPERIEMNT);
            }

            // TODO create experiment

            // TODO store result
        } catch (GerbilException e) {
            // TODO: handle exception
            // TODO store error
            experimentDAO.setExperimentTaskResult(experimentTaskId, e.getErrorType().getErrorCode());
        } catch (Exception e) {
            LOGGER.error("Error while trying to execute experiment.", e);
        }
    }
}
