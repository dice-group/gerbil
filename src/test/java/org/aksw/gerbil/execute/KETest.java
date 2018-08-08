package org.aksw.gerbil.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.annotator.TestAnnotatorConfiguration;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingResultStoringDAO4Debugging;
import org.aksw.gerbil.dataset.TestDataset;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.impl.ConfidenceBasedFMeasureCalculator;
import org.aksw.gerbil.execute.AbstractExperimentTaskTest.F1MeasureTestingObserver;
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
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class KETest extends AbstractExperimentTaskTest {

	@BeforeClass
	public static void setMatchingsCounterDebugFlag() {
		MatchingsCounterImpl.setPrintDebugMsg(true);
		ConfidenceBasedFMeasureCalculator.setPrintDebugMsg(true);
		ErrorCountingAnnotatorDecorator.setPrintDebugMsg(true);
	}
	
	private static final String TEXTS[] = new String[] {
	"Brad Smith is Microsoft s president and chief legal officer.In this role Smith is responsible for the company s corporate, external, and legal affairs." };
private static final Document GOLD_STD[] = new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
	Arrays.asList((Marking) new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
			new RelationImpl(new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
					new Annotation("http://dbpedia.org/ontology/president"),
					new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation")))),
			new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))))) };
private static final UriKBClassifier URI_KB_CLASSIFIER = new SimpleWhiteListBasedUriKBClassifier(
    "http://dbpedia.org/resource/");

@Parameters
public static Collection<Object[]> data() {
	List<Object[]> testConfigs = new ArrayList<Object[]>();
	testConfigs.add(new Object[] {
			new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
					Arrays.asList((Marking) new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
							new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))),
							new RelationImpl(new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
									new Annotation("http://dbpedia.org/ontology/president"),
									new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation")))))) },
			GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0 } });
	testConfigs.add(new Object[] {
				new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
						Arrays.asList((Marking) new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
					            "http://www.w3.org/2002/07/owl#Individual",
					            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))), 
								new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))),
								new RelationImpl(new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))),
										new Annotation("http://dbpedia.org/ontology/employer"),
										new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))), 
								new RelationImpl(new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
								            "http://www.w3.org/2002/07/owl#Individual",
								            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person"))),
												new Annotation("http://dbpedia.org/ontology/employer"),
												new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
								            "http://www.w3.org/2002/07/owl#Individual",
								            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))))))},
				GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 0.6666666666666666, 0.6666666666666666, 0.6666666666666666, 0.6666666666666666, 0.6666666666666666, 0.6666666666666666, 0 } });
	testConfigs.add(new Object[] {
				new Document[] { new DocumentImpl(TEXTS[0], "doc-0",
						Arrays.asList((Marking) new RelationImpl(
								new TypedNamedEntity(14, 23, "http://dbpedia.org/resource/Microsoft", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Organisation"))),
								new Annotation("http://dbpedia.org/ontology/president"),
								new TypedNamedEntity(0, 10, "http://aksw.org/notInWiki/Brad_Smith", new HashSet<String>(Arrays.asList(
						            "http://www.w3.org/2002/07/owl#Individual",
						            "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))))) },
				GOLD_STD, Matching.STRONG_ANNOTATION_MATCH, new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0 } });
	return testConfigs;
}	
	private Document annotatorResults[];
	private Document goldStandards[];
	private double expectedResults[];
	private Matching matching;

	public KETest(Document[] annotatorResults, Document[] goldStandards, Matching matching,
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
				new TestAnnotatorConfiguration(Arrays.asList(annotatorResults), ExperimentType.KE),
				new TestDataset(Arrays.asList(goldStandards), ExperimentType.KE), ExperimentType.KE, matching);
		runTest(experimentTaskId, experimentDAO, new EvaluatorFactory(URI_KB_CLASSIFIER), configuration,
				new F1MeasureTestingObserver(this, experimentTaskId, experimentDAO, expectedResults));
	}

}

