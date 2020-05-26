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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.InstanceListBasedDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.matching.impl.MatchingsCounterImpl;
import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.semantic.kb.UriKBClassifier;
import org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.RelationImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class OKE2018Task4Test extends AbstractExperimentTaskTest {

	@BeforeClass
	public static void setMatchingsCounterDebugFlag() {
		MatchingsCounterImpl.setPrintDebugMsg(true);
		ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
		ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);

	}

	private static final String TEXTS[] = new String[] {
			"Conor McGregor's longtime trainer, John Kavanagh, is ready to shock the world." };
	private static final Document GOLD_STD[] = new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
			Arrays.asList((Marking) new NamedEntity(0, 22, "http://dbpedia.org/resource/Conor_McGregor"),
					new RelationImpl(new NamedEntity(0, 22, "http://dbpedia.org/resource/Conor_McGregor"),
							new Annotation("http://dbpedia.org/ontology/trainer"),
							new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh")),
					new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh"))) };
    private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
            "http://dbpedia.org/resource/");
    
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testConfigs = new ArrayList<Object[]>();
		// The recognizer found everything, but marked the word "Movie"
		// additionally.
		testConfigs.add(new Object[] {
				new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
						Arrays.asList((Marking) new NamedEntity(0, 22, "http://dbpedia.org/resource/Conor_McGregor"),
								new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh"),
								new RelationImpl(new NamedEntity(0, 22, "http://dbpedia.org/resource/Conor_McGregor"),
										new Annotation("http://dbpedia.org/ontology/trainer"),
										new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh")))) },
				GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
		testConfigs.add(new Object[] {
				new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
						Arrays.asList((Marking) new RelationImpl(
								new NamedEntity(35, 48, "http://dbpedia.org/resource/John_Kavanagh"),
								new Annotation("http://dbpedia.org/ontology/trainer"),
								new NamedEntity(0, 22, "http://aksw.org/notInWiki/Conor_McGregor")))) },
				GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0 } });
		testConfigs.add(new Object[] {
				new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
						Arrays.asList((Marking) new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh"),
								new RelationImpl(new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh"),
										new Annotation("http://dbpedia.org/ontology/trainer"),
										new NamedEntity(0, 22, "http://aksw.org/notInWiki/Conor_McGregor")),
								new RelationImpl(new NamedEntity(0, 22, "http://www.wikidata.org/entity/Q5162259"),
										new Annotation("http://dbpedia.org/ontology/trainer"),
										new NamedEntity(35, 48, "http://aksw.org/notInWiki/John_Kavanagh")))) },
				GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 0.75, 0.75, 1/1.5,  0.75, 0.75, 1/1.5, 0 } });
		return testConfigs;
	}

	private Document annotatorResults[];
	private Document goldStandards[];
	private double expectedResults[];
	private Matching matching;

	public OKE2018Task4Test(Document[] annotatorResults, Document[] goldStandards, Matching matching,
			double[] expectedResults) {
		this.annotatorResults = annotatorResults;
		this.goldStandards = goldStandards;
		this.expectedResults = expectedResults;
		this.matching = matching;
	}

	@Test
	public void test() {
		int experimentTaskId = 1;
		SimpleLoggingResultStoringDAO4Debugging experimentDAO = new SimpleLoggingResultStoringDAO4Debugging();
		ExperimentTaskConfiguration configuration = new ExperimentTaskConfiguration(
				new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.OKE2018Task4),
				new InstanceListBasedDataset(Arrays.asList(goldStandards), ExperimentType.OKE2018Task4), ExperimentType.OKE2018Task4);
		runTest(experimentTaskId, experimentDAO, new HTTPBasedSameAsRetriever(), new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
				new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
	}

}
