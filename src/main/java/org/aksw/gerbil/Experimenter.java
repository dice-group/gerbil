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
package org.aksw.gerbil;

import java.util.Arrays;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.execute.AnnotatorOutputWriter;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.utils.ExpTaskConfigComparator;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Experimenter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Experimenter.class);

    private ExperimentTaskConfiguration configs[];
    private String experimentId;
    private ExperimentDAO experimentDAO;
    private Overseer overseer;
    private EvaluatorFactory evFactory;
    private AnnotatorOutputWriter annotatorOutputWriter = null;
    private SameAsRetriever globalRetriever = null;

    /**
     * Constructor
     * 
     * @deprecated Please use the other constructor and provide an
     *             {@link EvaluatorFactory}.
     */
    @Deprecated
    public Experimenter(Overseer overseer, ExperimentDAO experimentDAO, ExperimentTaskConfiguration configs[],
            String experimentId) {
        this.configs = configs;
        this.experimentId = experimentId;
        this.experimentDAO = experimentDAO;
        this.overseer = overseer;
        this.evFactory = new EvaluatorFactory();
    }

    public Experimenter(Overseer overseer, ExperimentDAO experimentDAO, SameAsRetriever globalRetriever, EvaluatorFactory evFactory,
             ExperimentTaskConfiguration configs[], String experimentId) {
        this.configs = configs;
        this.experimentId = experimentId;
        this.experimentDAO = experimentDAO;
        this.overseer = overseer;
        this.evFactory = evFactory;
        this.globalRetriever = globalRetriever;
    }

    @Override
    public void run() {
        Arrays.sort(configs, new ExpTaskConfigComparator());
        try {
            int taskId;
            for (int i = 0; i < configs.length; ++i) {
                if (couldHaveCachedResult(configs[i])) {
                    taskId = experimentDAO.connectCachedResultOrCreateTask(configs[i].annotatorConfig.getName(),
                            configs[i].datasetConfig.getName(), configs[i].type.name(),
                            experimentId);
                } else {
                    taskId = experimentDAO.createTask(configs[i].annotatorConfig.getName(),
                            configs[i].datasetConfig.getName(), configs[i].type.name(),
                            experimentId);
                }
                // If there is no experiment task result in the database
                if (taskId != ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED) {
                    // Create an executer which performs the task
                    ExperimentTask task = new ExperimentTask(taskId, experimentDAO, globalRetriever, evFactory,
                            configs[i]);
                    task.setAnnotatorOutputWriter(annotatorOutputWriter);
                    overseer.startTask(task);
                }
            }
            LOGGER.info("Experimenter finished the creation of tasks for experiment \"" + experimentId + "\"");
        } catch (Exception e) {
            LOGGER.error("Got an Exception while trying to start all needed tasks. Aborting the experiment.", e);
        }
    }

    private boolean couldHaveCachedResult(ExperimentTaskConfiguration config) {
        boolean couldBeCached = config.annotatorConfig.couldBeCached() && config.datasetConfig.couldBeCached();
        LOGGER.debug("Could be cached: {}.couldBeCached()={} && {}.couldBeCached()={} --> {}",
                config.annotatorConfig.getName(), config.annotatorConfig.couldBeCached(),
                config.datasetConfig.getName(), config.datasetConfig.couldBeCached(), couldBeCached);
        return couldBeCached;
    }

    public void setAnnotatorOutputWriter(AnnotatorOutputWriter annotatorOutputWriter) {
        this.annotatorOutputWriter = annotatorOutputWriter;
    }
}
