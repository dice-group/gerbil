package org.aksw.gerbil.execute;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.impl.ConfidenceScoreEvaluatorDecorator;
import org.aksw.gerbil.matching.scored.ScoredEvaluationCounts;
import org.aksw.gerbil.transfer.nif.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

@SuppressWarnings({ "deprecation", "unchecked" })
public class TempDetailedResultPrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempDetailedResultPrinter.class);

    public void printDetailedResults(ExperimentTaskStatus expResult, Dataset dataset, EvaluationResult<?> result) {
        Double confidenceScore = findAndDo(result,
                ConfidenceScoreEvaluatorDecorator.CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME,
                r -> ((EvaluationResult<Double>) r).getValue());
        if (confidenceScore == null) {
            LOGGER.warn("Didn't find a confidence score. Aborting.");
        }
        ScoredEvaluationCounts[][] counts = findAndDo(result, "perInstanceScoredEvaluationCounts",
                r -> ((EvaluationResult<ScoredEvaluationCounts[][]>) r).getValue());
        if (counts == null) {
            LOGGER.warn("Didn't find scored evaluation counts. Aborting.");
        }

        List<Document> documents = dataset.getInstances();
        String file = expResult.getType().getName() + "_" + expResult.getMatching() + "_" + expResult.getAnnotator()
                + "_" + expResult.getDataset() + ".csv";
        file = file.replace('/', '_');
        ScoredEvaluationCounts chosen;
        try (CSVWriter writer = new CSVWriter(new FileWriter(new File(file)))) {
            String line[] = new String[] { "uri", "confidence", "tp", "fp", "fn" };
            writer.writeNext(line);
            for (int i = 0; i < counts.length; ++i) {
                chosen = Stream.of(counts[i]).filter(c -> c.confidenceThreshould >= confidenceScore).findFirst()
                        .orElse(counts[i][counts[i].length - 1]);
                writeCountsToLine(line, documents.get(i).getDocumentURI(), chosen);
                writer.writeNext(line);
            }
        } catch (IOException e) {
            LOGGER.error("Error while trying to write detailed results.", e);
        }
    }

    public static <V> V findAndDo(EvaluationResult<?> result, String resultName,
            Function<EvaluationResult<?>, V> function) {
        if (resultName.equals(result.getName())) {
            return function.apply(result);
        } else if (result instanceof EvaluationResultContainer) {
            List<EvaluationResult<?>> tempResults = ((EvaluationResultContainer) result).getResults();
            V value;
            for (EvaluationResult<?> tempResult : tempResults) {
                value = findAndDo(tempResult, resultName, function);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    protected static void writeCountsToLine(String[] line, String uri, ScoredEvaluationCounts counts) {
        line[0] = uri;
        line[1] = Double.toString(counts.confidenceThreshould);
        line[2] = Integer.toString(counts.truePositives);
        line[3] = Integer.toString(counts.falsePositives);
        line[4] = Integer.toString(counts.falseNegatives);
    }
}
