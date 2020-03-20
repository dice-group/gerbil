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
package org.aksw.gerbil.dataset;

import java.lang.reflect.Constructor;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonDatasetConfigImpl extends DatasetConfigurationImpl implements ClosePermitionGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonDatasetConfigImpl.class);

    protected Dataset instance = null;
    protected int instanceUsages = 0;
    protected Semaphore instanceMutex = new Semaphore(1);

    public SingletonDatasetConfigImpl(String annotatorName, boolean couldBeCached,
                                      Constructor<? extends Dataset> constructor, Object[] constructorArgs,
                                      ExperimentType applicableForExperiment, EntityCheckerManager entityCheckerManager,
                                      SameAsRetriever globalRetriever) {
        super(annotatorName, couldBeCached, constructor, constructorArgs, applicableForExperiment, entityCheckerManager,
                globalRetriever);
    }

    @Override
    protected Dataset getPreparedDataset() throws Exception {
        instanceMutex.acquire();
        try {
            if (instance == null) {
                instance = super.getPreparedDataset();
                instance.setClosePermitionGranter(this);
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
            --instanceUsages;
            if (instanceUsages == 0) {
                instance = null;
                return true;
            } else {
                return false;
            }
        } finally {
            instanceMutex.release();
        }
    }
}
