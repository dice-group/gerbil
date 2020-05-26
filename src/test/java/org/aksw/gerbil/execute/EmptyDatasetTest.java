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

import java.util.Collections;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.InstanceListBasedDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the execution of an experiment with an empty dataset.
 * 
 * @author Michael R&ouml;der (mroeder@uni-paderborn.de)
 * 
 */
public class EmptyDatasetTest extends AbstractExperimentTaskTest {

    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.MT;
    private static final Matching MATCHING = Matching.WEAK_ANNOTATION_MATCH;

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        try {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(Collections.EMPTY_LIST, EXPERIMENT_TYPE),
                new InstanceListBasedDataset(Collections.EMPTY_LIST, EXPERIMENT_TYPE), EXPERIMENT_TYPE);
        runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(), configuration,
                new StatusCheckingTestTaskObserver(this, experimentTaskId, experimentDAO,
                        ErrorTypes.DATASET_EMPTY_ERROR.getErrorCode()));
        } catch(AssertionError e) {
            throw e;
        } catch(Exception e) {
            // not necessary but it makes Codacy happy
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
