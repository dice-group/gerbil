package org.aksw.gerbil.annotator.impl.qa;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.AnnotatorConfigurationImpl;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.check.impl.InMemoryCachingEntityCheckerManager;
import org.aksw.gerbil.dataset.impl.qald.QALDFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskState;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.execute.ExperimentTask;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.aksw.qa.systems.QANARY;

import com.google.common.collect.Sets;

public class QALD7QanaryFmeasureTest {

	public static void main(final String[] args) throws Exception {

		URL res = LoaderController.getLoadingAnchor().getResource("/QALD-master/7/data/qald-7-train-multilingual.json");
		File f = new File(res.toURI());

		String lang = "en";

		Object obj = ExtendedQALDJSONLoader.readJson(f);
		List<IQuestion> questions = EJQuestionFactory.getQuestionsFromJson(obj);

		File outputCsv = new File(f.getName() + "FMeasure_and_system_answers.csv");
		if (outputCsv.exists()) {
			outputCsv.delete();
		}
		FileWriter fWriter = new FileWriter(outputCsv);

		int i = 0;
		for (IQuestion q : questions) {

			System.out.println(String.format("\t\tQuestion %d/%d", i++, questions.size()));
			File temp = File.createTempFile(f.getName() + q.getId(), "json");
			temp.deleteOnExit();
			QaldJson singleQuestionJson = EJQuestionFactory.getQaldJson(Arrays.asList(q));
			ExtendedQALDJSONLoader.writeJson(singleQuestionJson, temp, true);
			String answerString = null;

			try {
				answerString = evaluateFile(temp, lang);
			} catch (Exception e) {
				answerString = e.getMessage();
			}

			String finalLine = String.format("%s; %s; %s; %s", q.getId(), q.getLanguageToQuestion().get(lang), answerString, q.getGoldenAnswers().toString());
			fWriter.append(finalLine + System.getProperty("line.separator"));

			System.out.println(finalLine);

		}

		fWriter.close();

	}

	public static String evaluateFile(final File f, final String lang) throws Exception {
		ExperimentType type = ExperimentType.QA;
		String annotator = "QANARY";
		String dataset = "QALD7 Train Multilingual";

		// Setting up the file as dataset, setting up qanary annotator

		ExperimentDAO dao = new SimpleLoggingDAO4Debugging();

		AnnotatorConfiguration annotatorConfig = new AnnotatorConfigurationImpl(annotator, false, ExtendedQALDBasedWebService.class.getConstructor(String.class),
		        new String[] { new QANARY().getQanaryUrl() }, ExperimentType.QA);
		EntityCheckerManager entityCheckerManager = new InMemoryCachingEntityCheckerManager();

		SameAsRetriever sameAsRetriever = null;

		DatasetConfiguration datasetConfig = new QALDFileDatasetConfig(dataset, f.getAbsolutePath(), false, type, entityCheckerManager, sameAsRetriever);

		ExperimentTaskConfiguration taskConfig = new ExperimentTaskConfiguration(annotatorConfig, datasetConfig, type, Matching.STRONG_ENTITY_MATCH, lang);

		ExperimentTask task = new ExperimentTask(0, dao, sameAsRetriever, new org.aksw.gerbil.evaluate.EvaluatorFactory(), taskConfig);

		taskConfig.setQuestionLanguage(lang);

		datasetConfig.setQuestionLanguage(lang);

		org.aksw.gerbil.dataset.Dataset gerbilDataset = datasetConfig.getDataset(type);
		Annotator wdaqua = annotatorConfig.getAnnotator(type);

		EvaluatorFactory evFactory = new org.aksw.gerbil.evaluate.EvaluatorFactory();
		List<Evaluator<?>> evaluators = new ArrayList<>();
		evaluators.add(new ResultSotringEvaluator<>());
		evFactory.addEvaluators(evaluators, taskConfig, gerbilDataset);

		ExperimentTaskState taskState = new ExperimentTaskState(gerbilDataset.size());

		//Using reflection to directly use org.aksw.gerbil.execute.ExperimentTask.runExperiment , to ease extraction of results

		Field taskTaskStateField = task.getClass().getDeclaredField("taskState");
		taskTaskStateField.setAccessible(true);
		taskTaskStateField.set(task, taskState);

		Method runExp = task.getClass().getDeclaredMethod("runExperiment", org.aksw.gerbil.dataset.Dataset.class, Annotator.class, List.class, ExperimentTaskState.class);
		runExp.setAccessible(true);

		EvaluationResultContainer result = (EvaluationResultContainer) runExp.invoke(task, gerbilDataset, wdaqua, evaluators, taskState);

		String answerSetString = "NO ANSWER";

		HashMap<String, String> erg = new HashMap<>();
		for (EvaluationResult it : result.getResults()) {
			if (it instanceof DoubleEvaluationResult) {
				erg.put(it.getName(), it.getValue().toString());
			}
			if (it instanceof StringEvaluationResult) {
				answerSetString = (String) it.getValue();
			}

		}

		String microPrecision = erg.get("Micro Precision");
		String microRecall = erg.get("Micro Recall");
		String microF1 = erg.get("Micro F1 score");

		String macroPrecision = erg.get("Macro Precision");
		String macroRecall = erg.get("Macro Recall");
		String macroF1 = erg.get("Macro F1 score");

		String answerString = String.format("%s; %s; %s; %s; %s; %s; %s", microPrecision, microRecall, microF1, macroPrecision, macroRecall, macroF1, answerSetString);

		return answerString;
	}

	static class ResultSotringEvaluator<T extends Marking, K> implements Evaluator<T> {

		@SuppressWarnings("unchecked")
		@Override
		public void evaluate(final List<List<T>> annotatorResults, final List<List<T>> goldStandard, final EvaluationResultContainer results) {
			Set<String> stringAnswerSet = Sets.newHashSet();
			for (List<T> resList : annotatorResults) {
				for (Marking it : resList) {
					if (it instanceof AnswerSet) {

						Set<K> answerset = ((AnswerSet<K>) it).getAnswers();
						//retrieve answers
						for (K unknownTypeIt : answerset) {
							try {
								Annotation o = (Annotation) unknownTypeIt;
								stringAnswerSet.addAll(o.getUris());
							} catch (Exception e) {
								String str = (String) unknownTypeIt;
								stringAnswerSet.add(str);
							}
						}

					}

					EvaluationResult res = new StringEvaluationResult("systemAnswers", stringAnswerSet.toString());
					results.addResult(res);
				}
			}
		}

	}

	static class StringEvaluationResult implements EvaluationResult {
		String name;
		String value;

		public StringEvaluationResult(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getValue() {
			return value;
		}

	}
}
