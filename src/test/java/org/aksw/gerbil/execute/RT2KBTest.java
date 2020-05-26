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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.impl.nif.NIFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.transfer.nif.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the entity linking evaluation.
 * 
 * @author Michael R&ouml;der <roeder@informatik.uni-leipzig.de>
 * 
 */
@RunWith(Parameterized.class)
public class RT2KBTest extends AbstractExperimentTaskTest {

	@BeforeClass
	public static void setMatchingsCounterDebugFlag() {
		MatchingsCounterImpl.setPrintDebugMsg(true);
		ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
		ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);

	}

	private static final DatasetConfiguration GOLD_STD = new NIFFileDatasetConfig("OKE_Task1",
			"src/test/resources/OKE_Challenge/example_data/task_rtkb.ttl", false, ExperimentType.RT2KB, null, null);
	private static final String ANNOTATOR_FILE_NAME = "src/test/resources/OKE_Challenge/example_data/task_rtkb_annotator.ttl";
	private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
			"http://www.ontologydesignpatterns.org/ont/");
	private static final EvaluatorFactory EVALUATOR_FACTORY = new EvaluatorFactory(URI_KB_CLASSIFIER);

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();
		testConfigs.add(new Object[] { ANNOTATOR_FILE_NAME, GOLD_STD, Matching.STRONG_ANNOTATION_MATCH,
				new double[] { 7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 0 } });
		return testConfigs;
	}
	
	private List<Document> annotatorResults;
	private DatasetConfiguration dataset;
	private double expectedResults[];
	private Matching matching;

	public RT2KBTest(String annotatorFile, DatasetConfiguration dataset, Matching matching,
			double[] expectedResults) throws GerbilException, FileNotFoundException {
		NIFParser parser = new TurtleNIFParser();
		this.annotatorResults= parser.parseNIF(new FileInputStream(annotatorFile));
		this.dataset = dataset;
		this.expectedResults = expectedResults;
		this.matching = matching;
	}

	@Test
	public void test() throws GerbilException {
		int experimentTaskId = 1;
		SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
		ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
				new TestAnnotatorConfiguration(this.annotatorResults, ExperimentType.RT2KB), dataset,
				ExperimentType.RT2KB);
		runTest(experimentTaskId, experimentDAO, EVALUATOR_FACTORY, configuration,
				new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
	}
}
