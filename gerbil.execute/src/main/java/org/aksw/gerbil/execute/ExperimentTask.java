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
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.annotator.decorator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.annotator.decorator.SingleInstanceSecuringAnnotatorDecorator;
import org.aksw.gerbil.annotator.decorator.TimeMeasuringAnnotatorDecorator;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.ResultNameToIdMapping;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentTaskState;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.evaluate.IntEvaluationResult;
import org.aksw.gerbil.evaluate.StringEvaluationResult;
import org.aksw.gerbil.evaluate.SubTaskResult;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
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
            dataset = configuration.datasetConfig.getDataset(configuration.type);
            if (dataset == null) {
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName() + "\" experimentType=\""
                        + configuration.type.name() + "\".", ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }
            //Clean up dataset
            List<Model> removeDocs = new ArrayList<Model>();
            for(Model d : dataset.getInstances()){
            	if(d.isEmpty()){
            		removeDocs.add(d);
            	}
            }
            dataset.getInstances().removeAll(removeDocs);

            // Create annotator
     
            annotator = (Annotator) configuration.annotatorConfig.getAnnotator(configuration.type);
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            List<Evaluator<?>> evaluators = new ArrayList<Evaluator<?>>();
            evFactory.addEvaluators(evaluators, configuration, dataset);
            
            Annotator decoratedAnnotator = annotator;
            // Add decroatoring evaluators
            TimeMeasuringAnnotatorDecorator timeMeasurer = TimeMeasuringAnnotatorDecorator
                    .createDecorator(configuration.type, decoratedAnnotator);
            if(timeMeasurer != null) {
                decoratedAnnotator = timeMeasurer;
                evaluators.add(timeMeasurer);
            }
            ErrorCountingAnnotatorDecorator errorCounter = ErrorCountingAnnotatorDecorator
                    .createDecorator(configuration.type, decoratedAnnotator, dataset.size());
            if(errorCounter != null) {
                decoratedAnnotator = errorCounter;
                evaluators.add(errorCounter);
            }
            Annotator securingDecorator = SingleInstanceSecuringAnnotatorDecorator.createDecorator(configuration.type,
                    decoratedAnnotator);
            if(securingDecorator != null) {
                decoratedAnnotator = securingDecorator;
            }

            // Prepare dataset for the experiment
            // prepareDataset(dataset);

            taskState = new ExperimentTaskState(dataset.size());
            // perform experiment
            EvaluationResult result = runExperiment(dataset, decoratedAnnotator, evaluators, taskState);
            evFactory.getConverterManager().close();
            // create result object
            // FIXME Fix this workaround
            ExperimentTaskResult expResult = new ExperimentTaskResult(configuration, new double[0],
                    ExperimentDAO.TASK_FINISHED, 0);
            switch(configuration.type){
            case SWC2018T1:
            case SWC1:	
            	transformResults(result, expResult);
            	break;
            case SWC_2019:
            case SWC2:
            	transformResults(result, expResult);
			default:
				break;
            }
            //set if task should be published
            expResult.setPublish(configuration.getPublish());
            //also set it in the subtasks
            if(expResult.getSubTasks()!=null) {
            	for(ExperimentTaskResult subResults : expResult.getSubTasks()) {
            		subResults.setPublish(configuration.getPublish());
            	}
            }
            // store result
            experimentDAO.setExperimentTaskResult(experimentTaskId, expResult);
            experimentDAO.setFile2SystemMapping(experimentTaskId, decoratedAnnotator.getFileMapping());
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
     * Prepares the given annotator results for the evaluation, i.e., performs a
     * sameAs retrieval if it is needed for the experiment type.
     * 
     * @param results
     * @param annotatorSameAsRetriever
     */
    @SuppressWarnings("deprecation")
    protected void prepareAnnotatorResults(List<? extends List<? extends Model>> results,
            SameAsRetriever annotatorSameAsRetriever) {
        switch (configuration.type) {
        case A2KB:// falls through
        case C2KB:
        case D2KB:
        case Rc2KB:
        case Sa2KB:
        case Sc2KB:
        case OKE_Task1: // falls through
        case OKE_Task2:
        case ETyping: {
            if (annotatorSameAsRetriever != null) {
//                for (List<? extends Model> result : results) {
//                    SameAsRetrieverUtils.addSameURIsToMeanings(annotatorSameAsRetriever, result);
//                }
            }
            return;
        }
        case ERec:// falls through
        default:
            // nothing to do
            return;
        }
    }

    protected void transformResults(EvaluationResult result, ExperimentTaskResult expResult) {
        if (result instanceof SubTaskResult) {
            ExperimentTaskResult subTask = new ExperimentTaskResult(((SubTaskResult) result).getConfiguration(),
                    new double[0], ExperimentDAO.TASK_FINISHED, 0);
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
//            switch (result.getName()) {
//            case ModelComparator.F1_SCORE_NAME: {
//                expResult.results[ExperimentTaskResult.F1_MEASURE_INDEX] = ((DoubleEvaluationResult) result)
//                        .getValueAsDouble();
//                return;
//            }
//            case ModelComparator.PRECISION_NAME: {
//                expResult.results[ExperimentTaskResult.PRECISION_INDEX] = ((DoubleEvaluationResult) result)
//                        .getValueAsDouble();
//                return;
//            }
//            case ModelComparator.RECALL_NAME: {
//                expResult.results[ExperimentTaskResult.RECALL_INDEX] = ((DoubleEvaluationResult) result)
//                        .getValueAsDouble();
//                return;
//            }
            
//            default: {
                int id = ResultNameToIdMapping.getInstance().getResultId(result.getName());
                if (id == ResultNameToIdMapping.UKNOWN_RESULT_TYPE) {
                    LOGGER.error("Got an unknown additional result \"" + result.getName() + "\". Discarding it.");
                } else {
                    expResult.addAdditionalResult(id, ((DoubleEvaluationResult) result).getValueAsDouble());

                }
            return;
        } else if (result instanceof StringEvaluationResult) {
            int id = ResultNameToIdMapping.getInstance().getResultId(result.getName());
            if (id == ResultNameToIdMapping.UKNOWN_RESULT_TYPE) {
                LOGGER.error("Got an unknown additional result \"" + result.getName() + "\". Discarding it.");
            } else {
//                expResult.addAdditionalResult(id, ((StringEvaluationResult) result).getValue().toString());
            	  expResult.setRoc(result.getValue().toString());
            }
        } else if (result instanceof IntEvaluationResult) {
            if (result.getName().equals(ErrorCountingAnnotatorDecorator.ERROR_COUNT_RESULT_NAME)) {
                expResult.errorCount = ((IntEvaluationResult) result).getValueAsInt();
                return;
            }
            int id = ResultNameToIdMapping.getInstance().getResultId(result.getName());
            if (id == ResultNameToIdMapping.UKNOWN_RESULT_TYPE) {
                LOGGER.error("Got an unknown additional result \"" + result.getName() + "\". Discarding it.");
            } else {
                expResult.addAdditionalResult(id, ((IntEvaluationResult) result).getValueAsInt());
            }
        }
    }

    protected EvaluationResult runExperiment(Dataset dataset, Annotator annotator,
            List<Evaluator<? extends Model>> evaluators, ExperimentTaskState state) throws GerbilException {
        EvaluationResult evalResult = null;
        switch (configuration.type) {
        case SWC2018T1: //falls through
        case SWC1: {
        	List<List<Model>> results = new ArrayList<List<Model>>(dataset.size());
            List<List<Model>> goldStandard = new ArrayList<List<Model>>(dataset.size());
        	SWCTask1System system  = ((SWCTask1System) annotator);
       		results.add(system.performTask1(dataset.getInstances().get(0)));
       		goldStandard.add(dataset.getInstances());
            taskState.increaseExperimentStepCount();
            if (annotatorOutputWriter != null) {
                annotatorOutputWriter.storeAnnotatorOutput(configuration, results, dataset.getInstances());
            }
            prepareAnnotatorResults(results, globalRetriever);
            evalResult = evaluate(evaluators, results, goldStandard);
            break;
        }
        case SWC_2019:
        case SWC2: {
        	List<List<Model>> results = new ArrayList<List<Model>>(dataset.size());
            List<List<Model>> goldStandard = new ArrayList<List<Model>>(dataset.size());
        	SWCTask2System system  = ((SWCTask2System) annotator);
       		results.add(system.performTask2(dataset.getInstances().get(0)));
       		goldStandard.add(dataset.getInstances());
            taskState.increaseExperimentStepCount();
            if (annotatorOutputWriter != null) {
                annotatorOutputWriter.storeAnnotatorOutput(configuration, results, dataset.getInstances());
            }
            prepareAnnotatorResults(results, globalRetriever);
            evalResult = evaluate(evaluators, results, goldStandard);
            break;
        }
        default:
            throw new GerbilException("This experiment type isn't implemented yet. Sorry for this.",
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        return evalResult;

    }

    @SuppressWarnings("unchecked")
    protected <T extends Model> EvaluationResult evaluate(List<Evaluator<? extends Model>> evaluators,
            List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        EvaluationResultContainer evalResults = new EvaluationResultContainer();
        for (Evaluator<? extends Model> e : evaluators) {
            ((Evaluator<T>) e).evaluate(annotatorResults, goldStandard, evalResults);
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
