package org.aksw.gerbil.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.ExperimentDAOImpl;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * <p>
 * This is a simple tool that can be used to transfer experiment results from a
 * source to a target database. It could be used to summarize single experiments
 * on a single experiment result page.
 * </p>
 * <p>
 * <b>Note</b> that this class is not in the focus of GERBIL and has not been
 * exhaustively tested like other classes of this project. Thus, the usage of
 * this class it <b>on your own risk</b>.
 * </p>
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DataMigrationTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationTool.class);

    private static final String SOURCE_DB_PATH = "../gerbil_database_1.2.2/gerbil.db";
    private static final String TARGET_DB_PATH = "./database_server/gerbil.db";

    /**
     * Experiment ID to which the experiment results should be copied.
     */

    private static final String TARGET_EXPERIMENT_ID = "201603170001";

    /**
     * Array of source experiment tasks that should be copied to the target
     * experiment. A single task description is a String array containing the
     * expriment id at [0], the annotator name at [1] and the dataset name at
     * [2].
     */
    private static final String SOURCE_EXPERIMENTS[][] = new String[][] { { "201603170000", "test", "test" } };
    private static final int SOURCE_EXPERIMENT_ID_INDEX = 0;
    private static final int SOURCE_ANNOTATOR_INDEX = 1;
    private static final int SOURCE_DATASET_INDEX = 2;

    public static void main(String[] args) {
        ExperimentDAO source = null;
        ExperimentDAO target = null;
        try {
            source = new ExperimentDAOImpl(
                    new SimpleDriverDataSource(new org.hsqldb.jdbc.JDBCDriver(), "jdbc:hsqldb:file:" + SOURCE_DB_PATH));
            source.initialize();
            target = new ExperimentDAOImpl(
                    new SimpleDriverDataSource(new org.hsqldb.jdbc.JDBCDriver(), "jdbc:hsqldb:file:" + TARGET_DB_PATH));
            target.initialize();
            performMigration(source, target);
        } finally {
            IOUtils.closeQuietly(source);
            IOUtils.closeQuietly(target);
        }
    }

    private static void performMigration(ExperimentDAO source, ExperimentDAO target) {
        // make sure the target experiment is not already existing
        List<ExperimentTaskStatus> targetExpTaskResults = target.getResultsOfExperiment(TARGET_EXPERIMENT_ID);
        if ((targetExpTaskResults != null) && (targetExpTaskResults.size() > 0)) {
            LOGGER.error("The target experiment {} is already existing. Aborting.", TARGET_EXPERIMENT_ID);
            return;
        }
        // retrieve the experiment task results we would like to migrate
        ExperimentTaskStatus migratingTasks[] = new ExperimentTaskStatus[SOURCE_EXPERIMENTS.length];
        Map<String, List<ExperimentTaskStatus>> cache = new HashMap<String, List<ExperimentTaskStatus>>();
        for (int i = 0; i < SOURCE_EXPERIMENTS.length; ++i) {
            migratingTasks[i] = retrieveTask(SOURCE_EXPERIMENTS[i], source, cache);
            if (migratingTasks[i] == null) {
                LOGGER.error("Couldn't retrieve experiment task {}. Aborting.", Arrays.toString(SOURCE_EXPERIMENTS[i]));
                return;
            } else {
                LOGGER.info("Found {}", Arrays.toString(SOURCE_EXPERIMENTS[i]));
            }
            prepare(migratingTasks[i]);
        }
        LOGGER.info("All experiment tasks have been retrieved successfully. Starting insetion...");
        int taskId;
        for (int i = 0; i < migratingTasks.length; ++i) {
            taskId = target.createTask(migratingTasks[i].annotator, migratingTasks[i].dataset,
                    migratingTasks[i].type.name(),  TARGET_EXPERIMENT_ID);
            target.setExperimentTaskResult(taskId, migratingTasks[i]);
            LOGGER.info("Inserted [{}, {}, {}] successfully.", TARGET_EXPERIMENT_ID, migratingTasks[i].annotator,
                    migratingTasks[i].dataset);
        }
        LOGGER.info("Finished.");
    }

    /**
     * Retrieve the experiment results described in the given String array.
     * 
     * @param experimentDesc
     * @param source
     * @param cache
     * @return
     */
    private static ExperimentTaskStatus retrieveTask(String[] experimentDesc, ExperimentDAO source,
            Map<String, List<ExperimentTaskStatus>> cache) {
        List<ExperimentTaskStatus> experimentTasks;
        if (cache.containsKey(experimentDesc[SOURCE_EXPERIMENT_ID_INDEX])) {
            experimentTasks = cache.get(experimentDesc[SOURCE_EXPERIMENT_ID_INDEX]);
        } else {
            experimentTasks = source.getResultsOfExperiment(experimentDesc[SOURCE_EXPERIMENT_ID_INDEX]);
            cache.put(experimentDesc[SOURCE_EXPERIMENT_ID_INDEX], experimentTasks);
        }
        if (experimentTasks == null) {
            LOGGER.error("Couldn't retrieve experiment {}. Returning null.",
                    experimentDesc[SOURCE_EXPERIMENT_ID_INDEX]);
            return null;
        }
        for (ExperimentTaskStatus result : experimentTasks) {
            if (result.annotator.equals(experimentDesc[SOURCE_ANNOTATOR_INDEX])
                    && result.dataset.equals(experimentDesc[SOURCE_DATASET_INDEX])) {
                return result;
            }
        }
        return null;
    }

    private static void prepare(ExperimentTaskStatus experimentTaskResult) {
        // Here, the task result can be prepared for the migration, e.g.,
        // transformed into a new version.
        // In most cases this is not needed and this method can be left empty
    }

}
