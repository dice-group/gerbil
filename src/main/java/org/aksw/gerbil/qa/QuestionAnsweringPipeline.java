package org.aksw.gerbil.qa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedJson;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.measure.AnswerBasedEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

//FIXME Micha@QA Was machen wir mit der Datei
public class QuestionAnsweringPipeline {
	private static Logger log = LoggerFactory
			.getLogger(QuestionAnsweringPipeline.class);

	public static void main(String[] args) {

		// read QALD question with gold SPAQRL query
		List<IQuestion> dataset = LoaderController
				.load(Dataset.QALD6_Train_Multilingual);

		ClassLoader.getSystemClassLoader();
		// read QANARY and QALD-HAWK answer to question
		// TODO write QANARY loader
		// List<IQuestion> answer =
		// QANARYReader.load(ClassLoader.getSystemResourceAsStream("QANARY_SystemAnswer_test.ttl"));
		log.debug("Load QALD JSON");
		InputStream in = ClassLoader.getSystemResourceAsStream("QALD_SystemAnswer_test.json");
		List<IQuestion> answer_qald_json = null;
		try {
			answer_qald_json = EJQuestionFactory.getQuestionsFromExtendedJson((ExtendedJson) ExtendedQALDJSONLoader.readJson(in, ExtendedJson.class));
		} catch (Exception e) {
			log.error("Could not load QALD JSON");
		}
		log.debug("Load QALD XML");
		List<IQuestion> answer_qald_xml = LoaderController.loadXML(ClassLoader
				.getSystemResourceAsStream("QALD_SystemAnswer_test.xml"));

		List<IQuestion> testQuestions = Lists.newArrayList(answer_qald_json);
		testQuestions.addAll(answer_qald_xml);
		for (IQuestion goldQuestion : dataset) {
			for (IQuestion systemQuestion : testQuestions) {
				if (systemQuestion.getLanguageToQuestion().get("en")
						.equals(goldQuestion.getLanguageToQuestion().get("en"))) {
					// extract concepts for C2KB
					Set<Node> concepts = extractResources(goldQuestion);
					log.debug("concepts: " + Joiner.on(", ").join(concepts));

					// property tagging 2KB
					Set<Node> properties = extractProperties(goldQuestion);
					log.debug("properties: " + Joiner.on(", ").join(properties));

					// relation extraction 2KB: extract triples directly
					// expressed
					// within the question
					Set<Triple> relations = extractRelations(goldQuestion);
					log.debug("relations: " + Joiner.on(", ").join(relations));

					// extract answer type 2KB (date, number, resource,..)
					String answerType = goldQuestion.getAnswerType();
					log.debug("answerType: " + answerType);

					// answer item type 2KB (class recognition + linking from
					// sentence)
					Set<Node> answerItemType = extractAnswerItemType(goldQuestion);
					log.debug("answerItemType: "
							+ Joiner.on(", ").join(answerItemType));

					// 1) Measure C2KB

					// 2) Measure P2KB

					// 3) Measure R2KB

					// 4) Measure AT2KB

					// 5) Measure AIT2KB

					// 6) Measure F-measure
					double precision = AnswerBasedEvaluation.precision(
							systemQuestion.getGoldenAnswers(), goldQuestion);
					double recall = AnswerBasedEvaluation.recall(
							systemQuestion.getGoldenAnswers(), goldQuestion);
					double fMeasure = AnswerBasedEvaluation.fMeasure(
							systemQuestion.getGoldenAnswers(), goldQuestion);
					log.debug("P=" + precision);
					log.debug("R=" + recall);
					log.debug("F=" + fMeasure);
				}
			}
		}
	}

	private static Set<Node> extractAnswerItemType(IQuestion question) {
		String sparqlQuerystring = question.getSparqlQuery();
		if (sparqlQuerystring == null) {
			return Sets.newHashSet();
		} else {
			Query sparqlQuery = QueryFactory.create(sparqlQuerystring);
			final Set<Node> candidates = Sets.newHashSet();
			ElementVisitorBase ELB = new ElementVisitorBase() {
				public void visit(ElementPathBlock el) {
					Iterator<TriplePath> triples = el.patternElts();
					while (triples.hasNext()) {
						TriplePath next = triples.next();
						// TODO @Axel: Least general generalization with
						// hierachical f-measure?
						if (next.getPredicate()
								.hasURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
							candidates.add(next.getObject());
						}
					}
				}
			};
			ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
			return candidates;
		}
	}

	private static Set<Triple> extractRelations(IQuestion question) {
		// TODO @Axel: Here relations do always have a variable in one of the
		// positions
		String sparqlQuerystring = question.getSparqlQuery();
		if (sparqlQuerystring == null) {
			return Sets.newHashSet();
		} else {
			Query sparqlQuery = QueryFactory.create(sparqlQuerystring);
			final Set<Triple> candidates = Sets.newHashSet();
			ElementVisitorBase ELB = new ElementVisitorBase() {
				public void visit(ElementPathBlock el) {
					Iterator<TriplePath> triples = el.patternElts();
					while (triples.hasNext()) {
						TriplePath next = triples.next();
						// TODO check what happens with FILTER and UNION
						candidates.add(next.asTriple());
					}
				}
			};
			ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
			return candidates;
		}
	}

	private static Set<Node> extractProperties(IQuestion question) {
		String sparqlQuerystring = question.getSparqlQuery();
		if (sparqlQuerystring == null) {
			return Sets.newHashSet();
		} else {
			Query sparqlQuery = QueryFactory.create(sparqlQuerystring);
			final Set<Node> candidates = Sets.newHashSet();
			ElementVisitorBase ELB = new ElementVisitorBase() {
				public void visit(ElementPathBlock el) {
					Iterator<TriplePath> triples = el.patternElts();
					while (triples.hasNext()) {
						TriplePath next = triples.next();
						Node predicate = next.getPredicate();
						// TODO leave out rdf:type
						candidates.add(predicate);
					}
				}
			};
			ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
			return candidates;
		}
	}

	private static Set<Node> extractResources(IQuestion question) {
		String sparqlQuerystring = question.getSparqlQuery();
		if (sparqlQuerystring == null) {
			return Sets.newHashSet();
		} else {
			Query sparqlQuery = QueryFactory.create(sparqlQuerystring);
			final Set<Node> candidates = Sets.newHashSet();
			ElementVisitorBase ELB = new ElementVisitorBase() {
				public void visit(ElementPathBlock el) {
					Iterator<TriplePath> triples = el.patternElts();
					while (triples.hasNext()) {
						TriplePath next = triples.next();
						Node subject = next.getSubject();
						candidates.add(subject);
						// TODO leave out classURIs (something rdf:type
						// classURI)

						if (next.getObject().isURI()) {
							Node object = next.getObject();
							candidates.add(object);
						}
					}
				}
			};
			ElementWalker.walk(sparqlQuery.getQueryPattern(), ELB);
			return candidates;
		}
	}
}
