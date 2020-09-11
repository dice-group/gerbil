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

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskState;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.IntEvaluationResult;
import org.aksw.gerbil.evaluate.SubTaskResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.semantic.sameas.SameAsRetrieverUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a single experiment designed as {@link Task} to be able to run
 * several tasks in parallel.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ExperimentTask implements Task {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentTask.class);

	private ExperimentDAO experimentDAO;
	private ExperimentTaskConfiguration configuration;
	private int experimentTaskId;
	private EvaluatorFactory evFactory;
	private ExperimentTaskState taskState = null;
	private AnnotatorOutputWriter annotatorOutputWriter = null;
	private SameAsRetriever globalRetriever = null;

	public ExperimentTask(int experimentTaskId, ExperimentDAO experimentDAO, SameAsRetriever globalRetriever,
			org.aksw.gerbil.evaluate.EvaluatorFactory evFactory, ExperimentTaskConfiguration configuration) {
		this.experimentDAO = experimentDAO;
		this.configuration = configuration;
		this.experimentTaskId = experimentTaskId;
		this.evFactory = evFactory;
		this.globalRetriever = globalRetriever;
	}

	@Override
	public void run() {
		LOGGER.info("Task started " + configuration.toString());
		Annotator annotator = null;
		Dataset dataset = null;
		try {
			// Create dataset
			String qLang = this.configuration.language;
			if (qLang == null || qLang.isEmpty()) {
				this.configuration.language = "en";
				qLang = "en";
			}
			// Create dataset
			dataset = configuration.datasetConfig.getDataset(configuration.type);
			if (dataset == null) {
				throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName() + "\" experimentType=\""
						+ configuration.type.name() + "\".", ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
			}
			// Check the dataset
			if (dataset.size() == 0) {
			    throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName() + "\" experimentType=\""
                        + configuration.type.name() + "\".", ErrorTypes.DATASET_EMPTY_ERROR);
			}

			// Create annotator
			annotator = (Annotator) configuration.annotatorConfig.getAnnotator(configuration.type);
			if (annotator == null) {
				throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getName()
						+ "\" experimentType=\"" + configuration.type.name() + "\".",
						ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
			}
			Annotator decoratedAnnotator = annotator;
			// Add decroatoring evaluators
//			TimeMeasuringAnnotatorDecorator timeMeasurer = TimeMeasuringAnnotatorDecorator
//					.createDecorator(configuration.type, decoratedAnnotator);
//			decoratedAnnotator = timeMeasurer;
//			ErrorCountingAnnotatorDecorator errorCounter = ErrorCountingAnnotatorDecorator
//					.createDecorator(configuration.type, decoratedAnnotator, dataset.size());
//			decoratedAnnotator = errorCounter;
//			decoratedAnnotator = SingleInstanceSecuringAnnotatorDecorator.createDecorator(configuration.type,
//					decoratedAnnotator);

			List<Evaluator<?>> evaluators = new ArrayList<Evaluator<?>>();
			evFactory.addEvaluators(evaluators, configuration, dataset);
//			evaluators.add(timeMeasurer);
//			evaluators.add(errorCounter);

			// Prepare dataset for the experiment
			// prepareDataset(dataset);

			taskState = new ExperimentTaskState(dataset.size());
			// perform experiment
			EvaluationResult result = runExperiment(dataset, decoratedAnnotator, evaluators, taskState);

			// create result object
			// FIXME Fix this workaround
			ExperimentTaskStatus expResult = new ExperimentTaskStatus(configuration, ExperimentDAO.TASK_FINISHED, 0);
			transformResults(result, expResult);

			// store result
			experimentDAO.setExperimentTaskResult(experimentTaskId, expResult);
			LOGGER.info("Task Finished " + configuration.toString());
		} catch (GerbilException e) {
			LOGGER.error("Got an error while running the task. Storing the error code in the db...", e);
			// store error
			experimentDAO.setExperimentState(experimentTaskId, e.getErrorType().getErrorCode());
		} catch (Exception e) {
			LOGGER.error("Error while trying to execute experiment.", e);
		} finally {
			IOUtils.closeQuietly(annotator);
			IOUtils.closeQuietly(dataset);
		}
	}

	/**
	 * Prepares the given dataset for the experiment, i.e., performs a sameAs
	 * retrieval if it is needed for the experiment type.
	 * 
	 * @param dataset
	 * @deprecated This should be done by the {@link DatasetConfiguration} class
	 *             that has loaded the dataset
	 */
	@Deprecated
	protected void prepareDataset(Dataset dataset) {
		switch (configuration.type) {
			//case MT:
			case WebNLG_RDF2Text:
			case WebNLG_Text2RDF:
			case NLG:
			case IR:
		default:
			// nothing to do
			return;
		}
	}

	/**
	 * Prepares the given annotator results for the evaluation, i.e., performs a
	 * sameAs retrieval if it is needed for the experiment type.
	 * 
	 * @param results
	 * @param annotatorSameAsRetriever
	 */
	protected void prepareAnnotatorResults(List<? extends List<? extends Meaning>> results,
			SameAsRetriever annotatorSameAsRetriever) {
		switch (configuration.type) {
		// relations need to be handled extra
			//case MT:
			case WebNLG_RDF2Text:
			case WebNLG_Text2RDF:
			case NLG:
			case IR:

		default:
			// nothing to do
			return;
		}
	}

	protected void prepareRelations(List<? extends List<? extends Marking>> results,
			SameAsRetriever annotatorSameAsRetriever) {
		if (annotatorSameAsRetriever != null) {

			for (List<? extends Marking> result : results) {
				SameAsRetrieverUtils.addSameURIsToMarkings(annotatorSameAsRetriever, result);
			}
		}
	}

	protected void transformResults(EvaluationResult result, ExperimentTaskStatus expResult) {
		TaskResult taskRes=null;
		String resName=result.getName();
		if (result instanceof SubTaskResult) {
			ExperimentTaskStatus subTask = new ExperimentTaskStatus(((SubTaskResult) result).getConfiguration(),ExperimentDAO.TASK_FINISHED, 0);
			List<EvaluationResult> tempResults = ((EvaluationResultContainer) result).getResults();
			for (EvaluationResult tempResult : tempResults) {
				transformResults(tempResult, subTask);
			}
			expResult.addSubTask(subTask);
		} else if (result instanceof EvaluationResultContainer) {
			List<EvaluationResult> tempResults = ((EvaluationResultContainer) result).getResults();
			for (EvaluationResult tempResult : tempResults) {
				transformResults(tempResult, expResult);
			}
		} else if (result instanceof DoubleEvaluationResult) {
			taskRes = new TaskResult(((DoubleEvaluationResult) result).getValueAsDouble(), "DOUBLE");
			
		} else if (result instanceof IntEvaluationResult) {
			taskRes = new TaskResult(((IntEvaluationResult) result).getValueAsInt(), "INT");
		}
		if(taskRes!=null && resName!=null) {
			expResult.getResultsMap().put(resName, taskRes);
		}
	}

	protected EvaluationResult runExperiment(Dataset dataset, Annotator annotator,
			List<Evaluator<? extends Marking>> evaluators, ExperimentTaskState state) throws GerbilException {
		EvaluationResult evalResult = null;
		switch (configuration.type) {
		//	case MT:
			case NLG:{
				List<List<SimpleFileRef>> results = new ArrayList<List<SimpleFileRef>>(dataset.size());
				List<List<SimpleFileRef>> goldStandard = new ArrayList<List<SimpleFileRef>>(dataset.size());

				// We assume that both lists only have one instance!
				Document tempDocument;
				tempDocument = dataset.getInstances().get(0);
				goldStandard.add(tempDocument.getMarkings(SimpleFileRef.class));

				Collection<Document> documents = ((InstanceListBasedAnnotator) annotator).getInstances();
				tempDocument = documents.iterator().next();
				results.add(tempDocument.getMarkings(SimpleFileRef.class));

				taskState.increaseExperimentStepCount();

				evalResult = evaluate(evaluators, results, goldStandard, configuration.language);
				break;
			}
			case IR:{
				List<List<SimpleFileRef>> results = new ArrayList<List<SimpleFileRef>>(dataset.size());
				List<List<SimpleFileRef>> goldStandard = new ArrayList<List<SimpleFileRef>>(dataset.size());

				// We assume that both lists only have one instance!
				Document tempDocument;
				tempDocument = dataset.getInstances().get(0);
				goldStandard.add(tempDocument.getMarkings(SimpleFileRef.class));

				Collection<Document> documents = ((InstanceListBasedAnnotator) annotator).getInstances();
				tempDocument = documents.iterator().next();
				results.add(tempDocument.getMarkings(SimpleFileRef.class));

				taskState.increaseExperimentStepCount();

				evalResult = evaluate(evaluators, results, goldStandard, configuration.language);
				break;
			}
			case WebNLG_RDF2Text:{
				List<List<SimpleFileRef>> results = new ArrayList<List<SimpleFileRef>>(dataset.size());
				List<List<SimpleFileRef>> goldStandard = new ArrayList<List<SimpleFileRef>>(dataset.size());

				// We assume that both lists only have one instance!
				Document tempDocument;
				tempDocument = dataset.getInstances().get(0);
				goldStandard.add(tempDocument.getMarkings(SimpleFileRef.class));

				Collection<Document> documents = ((InstanceListBasedAnnotator) annotator).getInstances();
				tempDocument = documents.iterator().next();
				results.add(tempDocument.getMarkings(SimpleFileRef.class));

				taskState.increaseExperimentStepCount();

				evalResult = evaluate(evaluators, results, goldStandard, configuration.language);
				break;
			}
			case WebNLG_Text2RDF:{
				List<List<SimpleFileRef>> results = new ArrayList<List<SimpleFileRef>>(dataset.size());
				List<List<SimpleFileRef>> goldStandard = new ArrayList<List<SimpleFileRef>>(dataset.size());

				// We assume that both lists only have one instance!
				Document tempDocument;
				tempDocument = dataset.getInstances().get(0);
				goldStandard.add(tempDocument.getMarkings(SimpleFileRef.class));

				Collection<Document> documents = ((InstanceListBasedAnnotator) annotator).getInstances();
				tempDocument = documents.iterator().next();
				results.add(tempDocument.getMarkings(SimpleFileRef.class));

				taskState.increaseExperimentStepCount();

				evalResult = evaluate(evaluators, results, goldStandard, configuration.language);
				break;
			}
		default:
			throw new GerbilException("This experiment type isn't implemented yet. Sorry for this.",
					ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		return evalResult;

	}

	@SuppressWarnings("unchecked")
	protected <T extends Marking> EvaluationResult evaluate(List<Evaluator<? extends Marking>> evaluators,
			List<List<T>> annotatorResults, List<List<T>> goldStandard, String language) {
		EvaluationResultContainer evalResults = new EvaluationResultContainer();
		for (Evaluator<? extends Marking> e : evaluators) {
			((Evaluator<T>) e).evaluate(annotatorResults, goldStandard, evalResults,language);
		}
		return evalResults;
	}

	@Override
	public String getId() {
		return configuration.toString();
	}

	@Override
	public String getProgress() {
		if (taskState != null) {
			return (taskState.getExperimentTaskProcess() * 100.0) + "% of dataset";
		} else {
			return null;
		}
	}

	public void setAnnotatorOutputWriter(AnnotatorOutputWriter annotatorOutputWriter) {
		this.annotatorOutputWriter = annotatorOutputWriter;
	}
}
