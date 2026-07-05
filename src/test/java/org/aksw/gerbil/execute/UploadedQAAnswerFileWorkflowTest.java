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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.web.config.AdapterList;
import org.aksw.gerbil.web.config.AdapterManager;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test for the QA workflow in which both the gold file and the
 * answer file are loaded from the configured upload directory.
 */
public class UploadedQAAnswerFileWorkflowTest extends AbstractExperimentTaskTest {

    private static final String UPLOADED_FILES_PATH_PROPERTY_KEY = "org.aksw.gerbil.UploadPath";
    private static final String TEST_DATASET_RESOURCE = "datasets/QALD_test.json";
    private static final String QUESTION_LANGUAGE = "en";
    private static final String GOLD_FILE_NAME = "gold_1.json";
    private static final String PREDICTION_FILE_NAME = "pred_1.json";
    private static final String UPLOADED_DATASET_TOKEN = "NIFDS_gold1(gold_1.json)";
    private static final String ANSWER_FILE_TOKEN = "AF_System(pred_1.json)(undefined)(AFDS_gold_1.json)";
    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.QA;
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;
    private static final double EXPECTED_RESULTS[] = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");

    @Test
    public void testUploadedQaDatasetAndAnswerFileWorkflow() throws Exception {
        String uploadPath = GerbilConfiguration.getInstance().getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
        Assert.assertNotNull("Missing upload path configuration: " + UPLOADED_FILES_PATH_PROPERTY_KEY, uploadPath);

        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        Assert.assertTrue("Couldn't prepare upload directory " + uploadDirectory.getAbsolutePath(),
                uploadDirectory.isDirectory());

        File goldUploadFile = new File(uploadDirectory, GOLD_FILE_NAME);
        File predictionUploadFile = new File(uploadDirectory, PREDICTION_FILE_NAME);
        boolean hadGoldFileBeforeTest = goldUploadFile.isFile();
        boolean hadPredictionFileBeforeTest = predictionUploadFile.isFile();
        byte[] previousGoldFileContent = hadGoldFileBeforeTest ? FileUtils.readFileToByteArray(goldUploadFile) : null;
        byte[] previousPredictionFileContent = hadPredictionFileBeforeTest
                ? FileUtils.readFileToByteArray(predictionUploadFile)
                : null;

        try {
            // Simulate files that have already been uploaded through the UI by
            // placing the gold dataset and the answer file in the configured upload
            // directory.
            copyResourceToFile(TEST_DATASET_RESOURCE, goldUploadFile);
            copyResourceToFile(TEST_DATASET_RESOURCE, predictionUploadFile);

            AdapterManager adapterManager = new AdapterManager();
            adapterManager.setAnnotators(new AdapterList<AnnotatorConfiguration>(
                    Collections.<AnnotatorConfiguration>emptyList()));
            adapterManager.setDatasets(
                    new AdapterList<DatasetConfiguration>(Collections.<DatasetConfiguration>emptyList()));

            // Resolve the uploaded dataset token once so that the NIFDS_* parsing path
            // is covered as well.
            DatasetConfiguration uploadedDatasetConfig = adapterManager.getDatasetConfig(UPLOADED_DATASET_TOKEN,
                    EXPERIMENT_TYPE, QUESTION_LANGUAGE);
            Assert.assertNotNull("Couldn't resolve uploaded dataset token " + UPLOADED_DATASET_TOKEN,
                    uploadedDatasetConfig);

            AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANSWER_FILE_TOKEN,
                    EXPERIMENT_TYPE, QUESTION_LANGUAGE);
            Assert.assertNotNull("Couldn't resolve uploaded answer-file token " + ANSWER_FILE_TOKEN, annotatorConfig);

            // MainController creates answer-file experiment tasks by resolving both the
            // annotator and the dataset from the AF_* token.
            DatasetConfiguration datasetConfig = adapterManager.getDatasetConfig(ANSWER_FILE_TOKEN, EXPERIMENT_TYPE,
                    QUESTION_LANGUAGE);
            Assert.assertNotNull("Couldn't resolve dataset from uploaded answer-file token " + ANSWER_FILE_TOKEN,
                    datasetConfig);

            int experimentTaskId = 1;
            SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
            ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(annotatorConfig, datasetConfig,
                    EXPERIMENT_TYPE, MATCHING, QUESTION_LANGUAGE);

            runTest(experimentTaskId, experimentDAO, null, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
                    new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, EXPECTED_RESULTS));
        } finally {
            restoreFile(goldUploadFile, hadGoldFileBeforeTest, previousGoldFileContent);
            restoreFile(predictionUploadFile, hadPredictionFileBeforeTest, previousPredictionFileContent);
        }
    }

    private static void copyResourceToFile(String resourceName, File targetFile) throws IOException {
        try (InputStream inputStream = UploadedQAAnswerFileWorkflowTest.class.getClassLoader()
                .getResourceAsStream(resourceName)) {
            Assert.assertNotNull("Couldn't find test resource " + resourceName, inputStream);
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
        }
    }

    private static void restoreFile(File file, boolean hadFileBeforeTest, byte[] previousFileContent)
            throws IOException {
        if (hadFileBeforeTest) {
            FileUtils.writeByteArrayToFile(file, previousFileContent);
        } else {
            FileUtils.deleteQuietly(file);
        }
    }
}
