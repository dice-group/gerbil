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
package org.aksw.gerbil.database;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author b.eickmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/database/database-context.xml" })
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExperimentDAOImplJUnitTest {

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;

    @Test
    public void testTaskCreation() {
        int taskId = this.dao.createTask("annotator1", "dataset1", "type1", "matching1", "id-23456");
        Assert.assertTrue(taskId > 0);
    }

    @Test
    public void testStateSettingAndGetting() {
        int taskId = this.dao.createTask("annotator1", "dataset1", "type1", "matching1", "id-456");
        int expectedState = (new Random()).nextInt();
        this.dao.setExperimentState(taskId, expectedState);
        int retrievedState = this.dao.getExperimentState(taskId);
        Assert.assertEquals(expectedState, retrievedState);
    }

    @Test
    public void testTaskCaching() throws InterruptedException {
        final long DURABILITY = 500;
        dao.setResultDurability(500);
        int firstTaskId = this.dao.createTask("annotator1", "dataset1", "type1", "matching1", "id-23456");
        // create the same task and test whether the already existing one is
        // reused
        int secondTaskId = this.dao.connectCachedResultOrCreateTask("annotator1", "dataset1", "type1", "matching1",
                "id-23457");
        Assert.assertTrue(secondTaskId + " != " + ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED,
                secondTaskId == ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED);

        // sleep and than create a third task for which the first task shouldn't
        // be reused
        Thread.sleep(DURABILITY);
        int thirdTaskId = this.dao.connectCachedResultOrCreateTask("annotator1", "dataset1", "type1", "matching1",
                "id-23458");
        Assert.assertFalse(ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED == thirdTaskId);
        Assert.assertFalse(firstTaskId == thirdTaskId);

        // set the third task to an error code. After that, create a new task
        // for which the third shouldn't be reused
        this.dao.setExperimentState(thirdTaskId, ErrorTypes.SERVER_STOPPED_WHILE_PROCESSING.getErrorCode());
        int fourthTaskId = this.dao.connectCachedResultOrCreateTask("annotator1", "dataset1", "type1", "matching1",
                "id-23459");
        Assert.assertFalse(ExperimentDAO.CACHED_EXPERIMENT_TASK_CAN_BE_USED == fourthTaskId);
        Assert.assertFalse(thirdTaskId == fourthTaskId);
    }

    @Test
    public void testExperimentCreationAndSelection() throws InterruptedException {
        final String EXPERIMENT_ID = "id-999";
        Set<ExperimentTaskResult> results = new HashSet<ExperimentTaskResult>();
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            if (i < 8) {
                results.add(new ExperimentTaskResult("annotator1", "dataset" + i, ExperimentType.D2KB,
                        Matching.STRONG_ANNOTATION_MATCH,
                        new double[] { random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(),
                                random.nextFloat(), random.nextFloat() },
                        ExperimentDAO.TASK_FINISHED, random.nextInt()));
            } else {
                results.add(new ExperimentTaskResult("annotator1", "dataset" + i, ExperimentType.D2KB,
                        Matching.STRONG_ANNOTATION_MATCH, new double[6],
                        i == 8 ? ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET
                                : ErrorTypes.UNEXPECTED_EXCEPTION.getErrorCode(),
                        0));
            }
        }

        int taskId;
        for (ExperimentTaskResult result : results) {
            taskId = this.dao.createTask(result.getAnnotator(), result.getDataset(), result.getType().name(),
                    result.getMatching().name(), EXPERIMENT_ID);
            if (result.state == ExperimentDAO.TASK_FINISHED) {
                this.dao.setExperimentTaskResult(taskId, result);
            } else {
                this.dao.setExperimentState(taskId, result.state);
            }
        }

        List<ExperimentTaskResult> retrievedResults = dao.getResultsOfExperiment(EXPERIMENT_ID);
        ExperimentTaskResult originalResult;
        for (ExperimentTaskResult retrievedResult : retrievedResults) {
            if (retrievedResult.state == ExperimentDAO.TASK_FINISHED) {
                Assert.assertTrue("Couldn't find " + retrievedResult.toString() + " inside of the expected results "
                        + results.toString(), results.remove(retrievedResult));
            } else {
                // We have to search them manually since the time stamps are
                // different
                originalResult = null;
                for (ExperimentTaskResult result : results) {
                    if ((result.state == retrievedResult.state) && (result.annotator.equals(retrievedResult.annotator))
                            && (result.dataset.equals(retrievedResult.dataset))
                            && (result.errorCount == retrievedResult.errorCount)
                            && (result.matching == retrievedResult.matching) && (result.type == retrievedResult.type)) {
                        originalResult = result;
                        break;
                    }
                }
                Assert.assertNotNull("Couldn't find " + retrievedResult.toString() + " inside of the expected results "
                        + results.toString(), originalResult);
                results.remove(originalResult);
            }
        }
        Assert.assertEquals("Not all expected results have been retrieved. Missing results " + results, 0,
                results.size());
    }

    @Test
    public void testSetRunningExperimentsToError() {
        int firstTaskId = this.dao.createTask("annotator1", "dataset1", "type1", "matching1", "id-23456");
        int retrievedState = this.dao.getExperimentState(firstTaskId);
        Assert.assertEquals(ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET, retrievedState);
        // simulate a restart of the server... this would cause a rerun of the
        // initialize method of the AbstractExperimentDAO class and a run of the
        // setRunningExperimentsToError method of the ExperimentDAOImpl class
        if (this.dao instanceof AbstractExperimentDAO) {
            ((AbstractExperimentDAO) this.dao).setRunningExperimentsToError();
            retrievedState = this.dao.getExperimentState(firstTaskId);
            Assert.assertEquals(ErrorTypes.SERVER_STOPPED_WHILE_PROCESSING.getErrorCode(), retrievedState);
        } else {
            System.err.println("WARNING: I didn't expected that the tested dao instance has not the type "
                    + AbstractExperimentDAO.class.getName());
        }
    }

    @Test
    public void testGetLatestResultsOfExperiments() {
        // Only the first task should be retrieved, the second is not finished,
        // the third has the wrong matching and the
        // fourth has the wrong type
        String tasks[][] = new String[][] {
                { "annotator1", "dataset1", ExperimentType.A2KB.name(), Matching.WEAK_ANNOTATION_MATCH.name() },
                { "annotator1", "dataset2", ExperimentType.A2KB.name(), Matching.WEAK_ANNOTATION_MATCH.name() },
                { "annotator1", "dataset1", ExperimentType.A2KB.name(), Matching.STRONG_ANNOTATION_MATCH.name() },
                { "annotator2", "dataset1", ExperimentType.D2KB.name(), Matching.WEAK_ANNOTATION_MATCH.name() } };
        int taskId;
        for (int i = 0; i < tasks.length; ++i) {
            taskId = this.dao.createTask(tasks[i][0], tasks[i][1], tasks[i][2], tasks[i][3], "id-" + i);
            if (i != 1) {
                this.dao.setExperimentState(taskId, ExperimentDAO.TASK_FINISHED);
            }
        }
        List<ExperimentTaskResult> results = this.dao.getLatestResultsOfExperiments(ExperimentType.A2KB.name(),
                Matching.WEAK_ANNOTATION_MATCH.name(), new String[] { "annotator1", "annotator2" },
                new String[] { "dataset1", "dataset2" });
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("annotator1", results.get(0).annotator);
        Assert.assertEquals("dataset1", results.get(0).dataset);
        Assert.assertEquals(0, results.get(0).state);
    }
}
