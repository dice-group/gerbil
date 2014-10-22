package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.execute.ExperimentTaskExecuter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Experimenter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Experimenter.class);

    private ExperimentTaskConfiguration configs[];
    private String experimentId;
    private ExperimentDAO experimentDAO;
    private WikipediaApiInterface wikiAPI;

    public Experimenter(WikipediaApiInterface wikiAPI, ExperimentDAO experimentDAO,
            ExperimentTaskConfiguration configs[], String experimentId) {
        this.configs = configs;
        this.experimentId = experimentId;
        this.experimentDAO = experimentDAO;
        this.wikiAPI = wikiAPI;
    }

    @Override
    public void run() {
        try {
            int taskId;
            for (int i = 0; i < configs.length; ++i) {
                if (couldHaveCachedResult(configs[i])) {
                    // FIXME If the result in the database contains an error code, wouldn't it be better to handle such
                    // a case as it wouldn't exist in the database?
                    taskId = experimentDAO.connectCachedResultOrCreateTask(
                            configs[i].annotatorConfig.getAnnotatorName(),
                            configs[i].datasetConfig.getDatasetName(), configs[i].type.name(),
                            configs[i].matching.name(), experimentId);
                } else {
                    taskId = experimentDAO.createTask(configs[i].annotatorConfig.getAnnotatorName(),
                            configs[i].datasetConfig.getDatasetName(), configs[i].type.name(),
                            configs[i].matching.name(), experimentId);
                }
                // If there is no experiment task result in the database
                if (taskId != ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED) {
                    // Create an executer which performs the task
                    ExperimentTaskExecuter executer = new ExperimentTaskExecuter(taskId, experimentDAO, configs[i],
                            wikiAPI);
                    Thread t = new Thread(executer);
                    t.start();
                }
            }
            LOGGER.info("Experimenter finished the creation of tasks for experiment \"" + experimentId + "\"");
        } catch (Exception e) {
            LOGGER.error("Got an Exception while trying to start all needed tasks. Aborting the experiment.", e);
        }
    }

    private boolean couldHaveCachedResult(ExperimentTaskConfiguration config) {
        LOGGER.error("Could be cached: " + config.annotatorConfig.getAnnotatorName() + ".couldBeCached()="
                + config.annotatorConfig.couldBeCached() + " && " + config.datasetConfig.getDatasetName()
                + ".couldBeCached()=" + config.datasetConfig.couldBeCached() + " --> "
                + (config.annotatorConfig.couldBeCached() && config.datasetConfig.couldBeCached()));
        return config.annotatorConfig.couldBeCached() && config.datasetConfig.couldBeCached();
    }
}
