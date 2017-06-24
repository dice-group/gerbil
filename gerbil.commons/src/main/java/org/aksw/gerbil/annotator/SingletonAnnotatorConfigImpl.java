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
package org.aksw.gerbil.annotator;

import java.lang.reflect.Constructor;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonAnnotatorConfigImpl extends AnnotatorConfigurationImpl implements ClosePermitionGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonAnnotatorConfigImpl.class);

    protected Annotator instance = null;
    protected int instanceUsages = 0;
    protected Semaphore instanceMutex = new Semaphore(1);

    public SingletonAnnotatorConfigImpl(String annotatorName, boolean couldBeCached,
            Constructor<? extends Annotator> constructor, Object constructorArgs[],
            ExperimentType applicableForExperiment) {
        super(annotatorName, couldBeCached, constructor, constructorArgs, applicableForExperiment);
    }

    protected Annotator loadAnnotator() throws Exception {
        instanceMutex.acquire();
        try {
            LOGGER.error("Instance requested. usages:" + instanceUsages);
            if (instance == null) {
                instance = constructor.newInstance(constructorArgs);
                instance.setName(this.getName());
                instance.setClosePermitionGranter(this);
                LOGGER.error("Instance created. usages:" + instanceUsages);
            }
            ++instanceUsages;
            return instance;
        } finally {
            instanceMutex.release();
        }
    }

    @Override
    public boolean givePermissionToClose() {
        try {
            instanceMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Couldn't get mutex to check whether the annotator should be closed. Returning false.");
            return false;
        }
        try {
            LOGGER.error("Close requested. usages:" + instanceUsages);
            --instanceUsages;
            if (instanceUsages == 0) {
                instance = null;
                LOGGER.error("Close permitted. usages:" + instanceUsages);
                return true;
            } else {
                return false;
            }
        } finally {
            instanceMutex.release();
        }
    }
}
