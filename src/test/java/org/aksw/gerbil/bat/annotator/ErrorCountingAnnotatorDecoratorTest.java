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
package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.C2WDataset;
import it.acubelab.batframework.problems.C2WSystem;
import it.acubelab.batframework.problems.TopicDataset;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.AnnotationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.annotators.AbstractAnnotatorConfiguration;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.datasets.AbstractDatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.junit.Assert;
import org.junit.Test;

public class ErrorCountingAnnotatorDecoratorTest {

    @Test
    public void testErrorCount() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(1, db, new ExperimentTaskConfiguration(
                new ErrorCausingAnnotatorConfig(5), new SimpleTestDatasetConfig(100), ExperimentType.C2KB,
                Matching.STRONG_ENTITY_MATCH), SingletonWikipediaApi.getInstance());
        task.run();
        ExperimentTaskResult result = db.getTaskResult(1);
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.errorCount);
        Assert.assertTrue(result.state >= 0);
    }

    @Test
    public void testTaskCanceling() {
        SimpleLoggingResultStoringDAO4Debugging db = new SimpleLoggingResultStoringDAO4Debugging();
        ExperimentTask task = new ExperimentTask(2, db, new ExperimentTaskConfiguration(
                new ErrorCausingAnnotatorConfig(30), new SimpleTestDatasetConfig(1000), ExperimentType.C2KB,
                Matching.STRONG_ENTITY_MATCH), SingletonWikipediaApi.getInstance());
        task.run();
        Assert.assertTrue(db.getExperimentState(2) < 0);
    }

    public static class ErrorCausingAnnotatorConfig extends AbstractAnnotatorConfiguration {

        private int errorsPerHundred;

        public ErrorCausingAnnotatorConfig(int errorsPerHundred) {
            super("Error causing topic system", false, ExperimentType.C2KB);
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        protected TopicSystem loadAnnotator(ExperimentType type) throws Exception {
            return new ErrorCausingTopicSystem(errorsPerHundred);
        }

    }

    public static class ErrorCausingTopicSystem implements C2WSystem {

        private int errorsPerHundred;
        private int errorsInThisHundred = 0;
        private int count = 0;

        public ErrorCausingTopicSystem(int errorsPerHundred) {
            super();
            this.errorsPerHundred = errorsPerHundred;
        }

        @Override
        public String getName() {
            return "Error causing topic system";
        }

        @Override
        public long getLastAnnotationTime() {
            return -1;
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            ++count;
            if (count > 100) {
                count -= 100;
                errorsInThisHundred = 0;
            }
            if (errorsInThisHundred < errorsPerHundred) {
                ++errorsInThisHundred;
                throw new AnnotationException("Test exception.");
            }
            return new HashSet<Tag>();
        }

    }

    public static class SimpleTestDatasetConfig extends AbstractDatasetConfiguration {

        private int size;

        public SimpleTestDatasetConfig(int size) {
            super("test dataset", false, ExperimentType.C2KB);
            this.size = size;
        }

        @Override
        protected TopicDataset loadDataset() throws Exception {
            return new SimpleTestDataset(size);
        }

    }

    public static class SimpleTestDataset implements C2WDataset {

        private String documents[];
        private List<HashSet<Tag>> gold;

        public SimpleTestDataset(int size) {
            documents = new String[size];
            Arrays.fill(documents, "");
            gold = new ArrayList<HashSet<Tag>>(size);
            HashSet<Tag> set = new HashSet<Tag>();
            for (int i = 0; i < size; i++) {
                gold.add(set);
            }
        }

        @Override
        public int getSize() {
            return documents.length;
        }

        @Override
        public String getName() {
            return "test dataset";
        }

        @Override
        public List<String> getTextInstanceList() {
            return Arrays.asList(documents);
        }

        @Override
        public int getTagsCount() {
            return 1;
        }

        @Override
        public List<HashSet<Tag>> getC2WGoldStandardList() {
            return gold;
        }

    }
}
