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

import org.aksw.gerbil.annotator.TestD2KBAnnotator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.EntityCheckerManagerImpl;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.ErrorFixingSameAsRetriever;
import org.aksw.gerbil.web.config.AdapterList;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.aksw.gerbil.web.config.RootConfig;
import org.junit.Assert;
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
public class D2KBGoldTest extends AbstractExperimentTaskTest {

    private static final ExperimentType EXPERIMENT_TYPE = ExperimentType.D2KB;
    private static final EvaluatorFactory EVALUATOR_FACTORY = RootConfig
            .createEvaluatorFactory(RootConfig.createSubClassInferencer());
    private static final SameAsRetriever SAME_AS_RETRIEVER = new ErrorFixingSameAsRetriever();
    private static final EntityCheckerManager ENTITY_CHECKER_MANAGER = new EntityCheckerManagerImpl();
    private static final Matching MATCHING = Matching.STRONG_ENTITY_MATCH;

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        AdapterList<DatasetConfiguration> adapterList = DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER,
                SAME_AS_RETRIEVER);
        List<DatasetConfiguration> datasetConfigs = adapterList.getAdaptersForExperiment(EXPERIMENT_TYPE);
//        for (DatasetConfiguration datasetConfig : datasetConfigs) {
//            testConfigs.add(new Object[] { datasetConfig });
//        }
        testConfigs.add(new Object[] { datasetConfigs.get(2) });
        return testConfigs;
    }

    public static void main(String[] args) throws GerbilException {
        AdapterList<DatasetConfiguration> adapterList = DatasetsConfig.datasets(ENTITY_CHECKER_MANAGER,
                SAME_AS_RETRIEVER);
        List<DatasetConfiguration> datasetConfigs = adapterList.getAdaptersForExperiment(EXPERIMENT_TYPE);
        for (DatasetConfiguration datasetConfig : datasetConfigs) {
            (new D2KBGoldTest(datasetConfig)).test();
        }
    }

    private DatasetConfiguration datasetConfig;

    public D2KBGoldTest(DatasetConfiguration datasetConfig) {
        this.datasetConfig = datasetConfig;
    }

    @Test
    public void test() throws GerbilException {
        int experimentTaskId = 1;
        SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();

        Dataset dataset = datasetConfig.getDataset(EXPERIMENT_TYPE);
        Assert.assertNotNull(dataset);

        // ExperimentTaskConfiguration configuration = new
        // ExperimentTaskConfiguration(
        // new TestD2KBAnnotator(copy(dataset.getInstances())), datasetConfig,
        // EXPERIMENT_TYPE, MATCHING);
        ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
                new TestD2KBAnnotator(dataset.getInstances()), datasetConfig, EXPERIMENT_TYPE, MATCHING);
        runTest(experimentTaskId, experimentDAO, SAME_AS_RETRIEVER, EVALUATOR_FACTORY, configuration, new F1MeasureTestingObserver(this,
                experimentTaskId, experimentDAO, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 }));
    }

    // private List<Document> copy(List<Document> instances) {
    // List<Document> copies = new ArrayList<Document>(instances.size());
    // for (Document document : instances) {
    // copies.add(copy(document));
    // }
    // return copies;
    // }
    //
    // private Document copy(Document document) {
    // return new DocumentImpl(document.getText(), document.getDocumentURI(),
    // copyMarkings(document.getMarkings()));
    // }
    //
    // private List<Marking> copyMarkings(List<Marking> markings) {
    // List<Marking> copies = new ArrayList<Marking>(markings.size());
    // for (Marking marking : markings) {
    // copies.add(copy(marking));
    // }
    // return copies;
    // }
    //
    // private Marking copy(Marking marking) {
    // if (marking instanceof ScoredTypedNamedEntity) {
    // return new ScoredTypedNamedEntity(startPosition, length, uris, types,
    // confidence);
    // }
    // return null;
    // }
    //
    // private Set<String> copyUris(Set<String> uris) {
    // Set<String> copy = new HashSet()<String>();
    // }
}
