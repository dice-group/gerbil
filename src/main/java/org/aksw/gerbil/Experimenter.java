/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Experimenter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Experimenter.class);

    private ExperimentTaskConfiguration configs[];
    private String experimentId;
    private ExperimentDAO experimentDAO;
    private WikipediaApiInterface wikiAPI;
    private Overseer overseer;

    public Experimenter(WikipediaApiInterface wikiAPI, Overseer overseer, ExperimentDAO experimentDAO,
            ExperimentTaskConfiguration configs[], String experimentId) {
        this.configs = configs;
        this.experimentId = experimentId;
        this.experimentDAO = experimentDAO;
        this.wikiAPI = wikiAPI;
        this.overseer = overseer;
    }

    @Override
    public void run() {
        try {
            int taskId;
            for (int i = 0; i < configs.length; ++i) {
                if (couldHaveCachedResult(configs[i])) {
                    taskId = experimentDAO.connectCachedResultOrCreateTask(configs[i].annotatorConfig.getName(),
                            configs[i].datasetConfig.getName(), configs[i].type.name(), configs[i].matching.name(),
                            experimentId);
                } else {
                    taskId = experimentDAO.createTask(configs[i].annotatorConfig.getName(),
                            configs[i].datasetConfig.getName(), configs[i].type.name(), configs[i].matching.name(),
                            experimentId);
                }
                // If there is no experiment task result in the database
                if (taskId != ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED) {
                    // Create an executer which performs the task
                    ExperimentTask task = new ExperimentTask(taskId, experimentDAO, configs[i], wikiAPI);
                    overseer.startTask(task);
                    // Thread t = new Thread(executer);
                    // t.start();
                    // if (SimpleThreadObserver.canObserveThread()) {
                    // t = new Thread(new SimpleThreadObserver(t));
                    // t.start();
                    // }
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
}
