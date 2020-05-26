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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.EntityCheckerManagerImpl;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.ErrorFixingSameAsRetriever;
import org.aksw.gerbil.web.config.AdapterList;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the evaluation by loading the gold standard and using it as
 * annotator result expecting a 1.0 as F1-score.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
@RunWith(Parameterized.class)
public class GoldStdEvalTest extends AbstractExperimentTaskTest {

    private static final EvaluatorFactory EVALUATOR_FACTORY = RootConfig
            .createEvaluatorFactory(RootConfig.createSubClassInferencer());
    private static final SameAsRetriever SAME_AS_RETRIEVER = new ErrorFixingSameAsRetriever();
    private static final EntityCheckerManager ENTITY_CHECKER_MANAGER = new EntityCheckerManagerImpl();
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

    @BeforeClass
    public static void setMatchingsCounterDebugFlag() {
        MatchingsCounterImpl.setPrintDebugMsg(false);
        ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(false);
        ErrorCountingAnnotatorDecorator.setPrintDebugMsg(false);
    }

    @Parameters
    public static Collection<Object[]> data() throws NoSuchFieldException, SecurityException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        AdapterList<DatasetConfiguration> adapterList = DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER,
                SAME_AS_RETRIEVER);
        List<DatasetConfiguration> datasetConfigs = null;
        for (ExperimentType experimentType : ExperimentType.values()) {
            if (ExperimentType.class.getDeclaredField(experimentType.name()).getAnnotation(Deprecated.class) == null) {
                datasetConfigs = adapterList.getAdaptersForExperiment(experimentType);
                for (DatasetConfiguration datasetConfig : datasetConfigs) {
                    testConfigs.add(new Object[] { experimentType, datasetConfig });
                }
            }
        }
        return testConfigs;
    }

    // public static void main(String[] args) throws GerbilException {
    // AdapterList<DatasetConfiguration> adapterList =
    // DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER,
    // SAME_AS_RETRIEVER);
    // List<DatasetConfiguration> datasetConfigs =
    // adapterList.getAdaptersForExperiment(EXPERIMENT_TYPE);
    // for (DatasetConfiguration datasetConfig : datasetConfigs) {
    // (new GoldStdEvalTest(datasetConfig)).test();
    // }
    // }

    private ExperimentType experimentType;
    private DatasetConfiguration datasetConfig;

    public GoldStdEvalTest(ExperimentType experimentType, DatasetConfiguration datasetConfig) {
        this.experimentType = experimentType;
        this.datasetConfig = datasetConfig;
    }

    @Test
    public void test() {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();

        Dataset dataset = null;
        try {
            dataset = datasetConfig.getDataset(experimentType);
        } catch (GerbilException e) {
            IOUtils.closeQuietly(experimentDAO);
            throw new RuntimeException("Couldn't load dataset " + datasetConfig.getName(), e);
        }
        Assert.assertNotNull(dataset);

        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestAnnotatorConfiguration(dataset.getInstances(), experimentType), datasetConfig, experimentType
        );
        runTest(experimentTaskId, experimentDAO, SAME_AS_RETRIEVER, EVALUATOR_FACTORY, configuration,
                new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO,
                        new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 }));
    }

}
