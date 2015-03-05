/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.execute;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.EntityLinker;
import org.aksw.gerbil.annotator.EntityRecognizer;
import org.aksw.gerbil.bat.annotator.ErrorCounter;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentTaskState;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFractory;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.simba.topicmodeling.concurrent.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentTask.class);

    private ExperimentDAO experimentDAO;
    private ExperimentTaskConfiguration configuration;
    private int experimentTaskId;
    @Deprecated
    private WikipediaApiInterface wikiAPI;
    private ExperimentTaskState taskState = null;

    public ExperimentTask(int experimentTaskId, ExperimentDAO experimentDAO, ExperimentTaskConfiguration configuration) {
        this.experimentDAO = experimentDAO;
        this.configuration = configuration;
        this.experimentTaskId = experimentTaskId;
    }

    @Override
    public void run() {
        LOGGER.info("Task started " + configuration.toString());
        try {
            // Create dataset
            Dataset dataset = (Dataset) configuration.datasetConfig.getDataset(configuration.type);
            if (dataset == null) {
                throw new GerbilException("dataset=\"" + configuration.datasetConfig.getName() + "\" experimentType=\""
                        + configuration.type.name() + "\".", ErrorTypes.DATASET_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // Create annotator
            Annotator annotator = (Annotator) configuration.annotatorConfig.getAnnotator(configuration.type);
            // TODO add time measuring
            // annotator =
            // TimeMeasuringAnnotatorDecorator.createDecorator(annotator);
            // annotator = (Annotator)
            // ErrorCountingAnnotatorDecorator.createDecorator((TopicSystem)
            // annotator,
            // dataset.size());
            if (annotator == null) {
                throw new GerbilException("annotator=\"" + configuration.annotatorConfig.getName()
                        + "\" experimentType=\"" + configuration.type.name() + "\".",
                        ErrorTypes.ANNOTATOR_DOES_NOT_SUPPORT_EXPERIMENT);
            }

            // create matching
            // MatchRelation<?> matching =
            // MatchingFactory.createMatchRelation(wikiAPI,
            // configuration.matching,
            // configuration.type);
            Evaluator evaluator = EvaluatorFractory.createEvaluators(configuration);
            // if (matching == null) {
            // throw new GerbilException("matching=\"" +
            // configuration.matching.name() + "\" experimentType=\""
            // + configuration.type.name() + "\".",
            // ErrorTypes.MATCHING_DOES_NOT_SUPPORT_EXPERIMENT);
            // }

            taskState = new ExperimentTaskState(dataset.size());
            // perform experiment
            EvaluationResult result = runExperiment(dataset, annotator, evaluator, taskState);

            int errorCount = 0;
            if (annotator instanceof ErrorCounter) {
                errorCount = ((ErrorCounter) annotator).getErrorCount();
            }
            // create result object
            // FIXME Fix this workaround
            double results[] = new double[6];
            transformResults(result, results);
            ExperimentTaskResult expResult = new ExperimentTaskResult(configuration, results,
                    ExperimentDAO.TASK_FINISHED, errorCount);

            // store result
            experimentDAO.setExperimentTaskResult(experimentTaskId, expResult);
            LOGGER.info("Task Finished " + configuration.toString());
        } catch (GerbilException e) {
            LOGGER.error("Got an error while running the task. Storing the error code in the db...", e);
            // store error
            experimentDAO.setExperimentState(experimentTaskId, e.getErrorType().getErrorCode());
        } catch (Exception e) {
            LOGGER.error("Error while trying to execute experiment.", e);
        }
    }

    private void transformResults(EvaluationResult result, double results[]) {
        if (result instanceof EvaluationResultContainer) {
            List<EvaluationResult> tempResults = ((EvaluationResultContainer) result).getResults();
            for (EvaluationResult tempResult : tempResults) {
                transformResults(tempResult, results);
            }
        } else if (result instanceof DoubleEvaluationResult) {
            switch (result.getName()) {
            case FMeasureCalculator.MACRO_F1_SCORE_NAME: {
                results[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_PRECISION_NAME: {
                results[ExperimentTaskResult.MACRO_PRECISION_INDEX] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MACRO_RECALL_NAME: {
                results[ExperimentTaskResult.MACRO_RECALL_INDEX] = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_F1_SCORE_NAME: {
                results[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_PRECISION_NAME: {
                results[ExperimentTaskResult.MICRO_PRECISION_INDEX] = ((DoubleEvaluationResult) result)
                        .getValueAsDouble();
                break;
            }
            case FMeasureCalculator.MICRO_RECALL_NAME: {
                results[ExperimentTaskResult.MICRO_RECALL_INDEX] = ((DoubleEvaluationResult) result).getValueAsDouble();
                break;
            }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private EvaluationResult runExperiment(Dataset dataset, Annotator annotator, Evaluator<?> evaluator,
            ExperimentTaskState state) throws GerbilException {
        EvaluationResult evalResult = null;
        switch (configuration.type) {
        case D2KB:
        case EntityLinking: {
            try {
                List<List<NamedEntity>> results = new ArrayList<List<NamedEntity>>(dataset.size());
                List<List<NamedEntity>> goldStandard = new ArrayList<List<NamedEntity>>(dataset.size());
                EntityLinker linker = ((EntityLinker) annotator);
                for (Document document : dataset.getInstances()) {
                    results.add(linker.performLinking(document));
                    goldStandard.add(document.getMarkings(NamedEntity.class));
                }
                evalResult = ((Evaluator<NamedEntity>) evaluator).evaluate(results, goldStandard);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case A2KB:
        case Sa2KB:
        case EntityExtraction: {
            try {
                List<List<NamedEntity>> results = new ArrayList<List<NamedEntity>>(dataset.size());
                List<List<NamedEntity>> goldStandard = new ArrayList<List<NamedEntity>>(dataset.size());
                EntityLinker linker = ((EntityLinker) annotator);
                for (Document document : dataset.getInstances()) {
                    results.add(linker.performLinking(document));
                    goldStandard.add(document.getMarkings(NamedEntity.class));
                }
                evalResult = ((Evaluator<NamedEntity>) evaluator).evaluate(results, goldStandard);
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        case C2KB: {
            // Vector<C2WSystem> c2wAnnotator = new Vector<C2WSystem>(1);
            // c2wAnnotator.add((C2WSystem) annotator);
            // Vector<C2WDataset> c2wDataset = new Vector<C2WDataset>(1);
            // c2wDataset.add((C2WDataset) dataset);
            // Vector<MatchRelation<Tag>> matchings = new
            // Vector<MatchRelation<Tag>>(1);
            // matchings.add((MatchRelation<Tag>) matching);
            // try {
            // results = RunExperiments.performC2WExpVarThreshold(matchings,
            // null, null, null, c2wAnnotator,
            // c2wDataset, state, wikiAPI);
            // // LOGGER.info("average time needed by {} on {}: {}",
            // // annotator.getName(), dataset.getName(),
            // // BenchmarkCache.getAvgC2WTimingsForDataset(annotator.getName(),
            // // dataset.getName()));
            // } catch (Exception e) {
            throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
            // }
            // break;
        }
        case Sc2KB: // Falls through
        case Rc2KB: {
            // Vector<Sc2WSystem> rc2wAnnotator = new Vector<Sc2WSystem>(1);
            // rc2wAnnotator.add((Sc2WSystem) annotator);
            // Vector<C2WDataset> rc2wDataset = new Vector<C2WDataset>(1);
            // rc2wDataset.add((C2WDataset) dataset);
            // Vector<MatchRelation<Tag>> matchings = new
            // Vector<MatchRelation<Tag>>(1);
            // matchings.add((MatchRelation<Tag>) matching);
            // try {
            // results = RunExperiments.performC2WExpVarThreshold(matchings,
            // null, null, rc2wAnnotator, null,
            // rc2wDataset, state, wikiAPI);
            // } catch (Exception e) {
            throw new GerbilException(ErrorTypes.UNEXPECTED_EXCEPTION);
            // }
            // break;
        }
        case EntityRecognition: {
            try {
                List<List<Span>> results = new ArrayList<List<Span>>(dataset.size());
                List<List<Span>> goldStandard = new ArrayList<List<Span>>(dataset.size());
                EntityRecognizer recognizer = ((EntityRecognizer) annotator);
                for (Document document : dataset.getInstances()) {
                    results.add(recognizer.performRecognition(document));
                    goldStandard.add(document.getMarkings(Span.class));
                }
                evalResult = null; // TODO get evaluation result from evaluation
                                   // object
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.UNEXPECTED_EXCEPTION);
            }
            break;
        }
        default:
            throw new GerbilException("This experiment type isn't implemented yet. Sorry for this.",
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        return evalResult;
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

}
