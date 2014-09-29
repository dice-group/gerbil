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
@ContextConfiguration(locations = {
        "file:src/main/resources/spring/database/database-context.xml"
})
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
        int secondTaskId = this.dao.connectCachedResultOrCreateTask("annotator1", "dataset1", "type1", "matching1",
                "id-23457");
        Assert.assertTrue(firstTaskId == secondTaskId);

        Thread.sleep(DURABILITY);

        int thirdTaskId = this.dao.connectCachedResultOrCreateTask("annotator1", "dataset1", "type1", "matching1",
                "id-23458");
        Assert.assertFalse(firstTaskId == thirdTaskId);
    }

    @Test
    public void testExperimentCreationAndSelection() throws InterruptedException {
        final String EXPERIMENT_ID = "id-999";
        Set<ExperimentTaskResult> results = new HashSet<ExperimentTaskResult>();
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            if (i < 8) {
                results.add(new ExperimentTaskResult("annotator1", "dataset" + i,
                        ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH, new double[] { random.nextFloat(),
                                random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(),
                                random.nextFloat() }, ExperimentDAO.TASK_FINISHED, random.nextInt()));
            } else {
                results.add(new ExperimentTaskResult("annotator1", "dataset" + i,
                        ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH, new double[6],
                        i == 8 ? ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET
                                : ErrorTypes.UNEXPECTED_EXCEPTION.getErrorCode(), 0));
            }
        }

        int taskId;
        for (ExperimentTaskResult result : results) {
            taskId = this.dao.createTask(result.getAnnotator(), result.getDataset(), result.getType().name(), result
                    .getMatching().name(), EXPERIMENT_ID);
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
                // We have to search them manually since the time stamps are different
                originalResult = null;
                for (ExperimentTaskResult result : results) {
                    if ((result.state == retrievedResult.state) && (result.annotator.equals(retrievedResult.annotator))
                            && (result.dataset.equals(retrievedResult.dataset))
                            && (result.errorCount == retrievedResult.errorCount)
                            && (result.matching == retrievedResult.matching)
                            && (result.type == retrievedResult.type)) {
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
}
