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
package org.aksw.gerbil.web;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.DatasetMetaData;
import org.aksw.gerbil.utils.DatasetMetaDataMapping;
import org.aksw.gerbil.utils.PearsonsSampleCorrelationCoefficient;
import org.aksw.gerbil.web.config.AdapterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ExperimentOverviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentOverviewController.class);

    private static final double NOT_AVAILABLE_SENTINAL = -2;
    private static final int MIN_NUMBER_OF_VALUES_FOR_CORR_CALC = 5;
    private static final String CORRELATION_TABLE_COLUMN_HEADINGS[] = { "number of documents", "avg. document length",
            "number of entities", "entities per document", "entities per token", "amount of persons",
            "amount of organizations", "amount of locations", "amount of others"/*
                                                                                 * , "corr. based on # datasets"
                                                                                 */ };

    // The leaderboard takes too much time at the moment!
    private static final boolean SHOW_LEADERBOARD = false;

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO dao;

    @Autowired
    @Qualifier("annotators")
    private AdapterList<AnnotatorConfiguration> annotators;

    @Autowired
    @Qualifier("datasets")
    private AdapterList<DatasetConfiguration> datasets;

    @RequestMapping("/experimentoverview")
    public @ResponseBody String experimentoverview(@RequestParam(value = "experimentType") String experimentType,
            @RequestParam(value = "matching") String matchingString) {
        LOGGER.debug("Got request on /experimentoverview(experimentType={}, matching={}", experimentType,
                matchingString);
        Matching matching = MainController.getMatching(matchingString);
        ExperimentType eType = ExperimentType.valueOf(experimentType);

        String annotatorNames[] = loadAnnotators(eType);
        String datasetNames[] = loadDatasets(eType);
        String languages[] = loadLanguages();
        String leaderBoard = "{}";
        if (SHOW_LEADERBOARD) {
            // This will get _all_ annotators that GEBRIL ever saw. This is way too much!!!
            // We need to find a better way to handle this during a challenge, e.g., limit
            // it to the datasets that are important for the challenge
            leaderBoard = loadLeaderBoard(eType, loadAnnotatorsFromDB(), datasetNames, languages, null);
        }
        double results[][] = loadLatestResults(eType, matching, annotatorNames, datasetNames, languages);
        double correlations[][] = calculateCorrelations(results, datasetNames);
        return generateJson(results, correlations, annotatorNames, datasetNames, languages, leaderBoard);

    }

    @SuppressWarnings("unused")
    private String[] loadAnnotatorsFromDB() {
        Set<String> annotatorNames = dao.getAnnotators();
        String annotatorNameArray[] = annotatorNames.toArray(new String[annotatorNames.size()]);
        Arrays.sort(annotatorNameArray);
        return annotatorNameArray;
    }

    private String[] loadLanguages() {
        Set<String> languages = dao.getAllLangauges();
        String languagesArray[] = languages.toArray(new String[languages.size()]);
        Arrays.sort(languagesArray);
        return languagesArray;
    }

    private double[][] loadLatestResults(ExperimentType experimentType, Matching matching, String[] annotatorNames,
            String[] datasetNames, String[] languages) {
        Map<String, Integer> annotator2Index = new HashMap<String, Integer>();
        for (int i = 0; i < annotatorNames.length; ++i) {
            annotator2Index.put(annotatorNames[i], i);
        }
        Map<String, Integer> dataset2Index = new HashMap<String, Integer>();
        for (int i = 0; i < datasetNames.length; ++i) {
            dataset2Index.put(datasetNames[i], i);
        }

        List<ExperimentTaskResult> expResults = dao.getLatestResultsOfExperiments(experimentType.name(),
                matching.name(), annotatorNames, datasetNames, languages);
        double results[][] = new double[annotatorNames.length][datasetNames.length];
        for (int i = 0; i < results.length; ++i) {
            Arrays.fill(results[i], NOT_AVAILABLE_SENTINAL);
        }
        int row, col;
        for (ExperimentTaskResult result : expResults) {
            if (annotator2Index.containsKey(result.annotator) && dataset2Index.containsKey(result.dataset)) {
                row = annotator2Index.get(result.annotator);
                col = dataset2Index.get(result.dataset);
                if (result.state == ExperimentDAO.TASK_FINISHED) {
                    results[row][col] = result.getMicroF1Measure();
                } else {
                    results[row][col] = result.state;
                }
            }
        }
        return results;
    }

    private String loadLeaderBoard(ExperimentType experimentType, String[] annotatorNames, String[] datasetNames,
            String[] languages, Calendar challengeDate) {
        LOGGER.info("Loading best results for {} languages, {} datasets and {} systems.", languages.length,
                datasetNames.length, annotatorNames.length);

        StringBuilder listsAsJson = new StringBuilder("{ \"datasets\": [ {");

        int count = 0;
        for (String language : languages) {
            for (String dataset : datasetNames) {
                List<ExperimentTaskResult> leaderList = new ArrayList<ExperimentTaskResult>();

                listsAsJson.append("\"language\" : \"").append(language).append("\" , ");
                listsAsJson.append("\"datasetName\" : \"").append(dataset).append("\", ");
                for (String annotator : annotatorNames) {
                    LOGGER.info("Looking at system {}...", annotator);
                    ExperimentTaskResult result;
                    if (challengeDate == null) {
                        result = dao.getBestResult(experimentType.name(), annotator, dataset, language);
                    } else {

                        result = dao.getBestResult(experimentType.name(), annotator, dataset, language,
                                new Timestamp(challengeDate.getTimeInMillis()));
                    }
                    if (result != null) {
                        leaderList.add(result);
                    }
                }
                Collections.sort(leaderList, new LeaderBoardComparator(experimentType));
                // Get only top 10
                leaderList = leaderList.subList(0, Math.min(10, leaderList.size()));
                listsAsJson.append(" \"leader\" : [");
                int count2 = 0;
                for (ExperimentTaskResult expResults : leaderList) {
                    String annotator = expResults.annotator;
                    listsAsJson.append("{ \"annotatorName\" : \"").append(annotator).append("\", \"macrof1\": \"");
                    listsAsJson.append(expResults.results[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX])
                            .append("\", \"microf1\": \"")
                            .append(expResults.results[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX]).append("\"}");

                    if (count2 < leaderList.size() - 1) {
                        listsAsJson.append(", ");
                    }

                    count2++;
                }
                listsAsJson.append("]}");
                if (count < datasetNames.length - 1)
                    listsAsJson.append(", {");
                count++;
            }
        }

        listsAsJson.append("]}");

        return listsAsJson.toString();
    }

    private String[] loadAnnotators(ExperimentType eType) {
        // get annotators from table
        Set<String> annotatorNames = annotators.getAdapterNamesForExperiment(eType);
        String annotatorNameArray[] = annotatorNames.toArray(new String[annotatorNames.size()]);
        Arrays.sort(annotatorNameArray);
        return annotatorNameArray;
    }

    private String[] loadDatasets(ExperimentType eType) {
        // get datasets from table
        Set<String> datasetNames = datasets.getAdapterNamesForExperiment(eType);
        String datasetNameArray[] = datasetNames.toArray(new String[datasetNames.size()]);
        Arrays.sort(datasetNameArray);
        return datasetNameArray;
    }

    private double[][] calculateCorrelations(double[][] results, String datasetNames[]) {
        DatasetMetaDataMapping mapping = DatasetMetaDataMapping.getInstance();
        DatasetMetaData metadata[] = new DatasetMetaData[datasetNames.length];
        for (int i = 0; i < datasetNames.length; ++i) {
            metadata[i] = mapping.getMetaData(datasetNames[i]);
        }
        double correlations[][] = new double[results.length][CORRELATION_TABLE_COLUMN_HEADINGS.length];
        DoubleArrayList annotatorResults = new DoubleArrayList(datasetNames.length);
        DoubleArrayList numberOfDocuments = new DoubleArrayList(datasetNames.length);
        DoubleArrayList avgDocumentLength = new DoubleArrayList(datasetNames.length);
        DoubleArrayList numberOfEntities = new DoubleArrayList(datasetNames.length);
        DoubleArrayList entitiesPerDoc = new DoubleArrayList(datasetNames.length);
        DoubleArrayList entitiesPerToken = new DoubleArrayList(datasetNames.length);
        DoubleArrayList amountOfPersons = new DoubleArrayList(datasetNames.length);
        DoubleArrayList amountOfOrganizations = new DoubleArrayList(datasetNames.length);
        DoubleArrayList amountOfLocations = new DoubleArrayList(datasetNames.length);
        DoubleArrayList amountOfOthers = new DoubleArrayList(datasetNames.length);
        double annotatorResultsAsArray[];
        int elementCount;
        for (int i = 0; i < correlations.length; ++i) {
            Arrays.fill(correlations[i], NOT_AVAILABLE_SENTINAL);
            // load the values for this annotator
            annotatorResults.clear();
            numberOfDocuments.clear();
            avgDocumentLength.clear();
            numberOfEntities.clear();
            entitiesPerDoc.clear();
            entitiesPerToken.clear();
            amountOfPersons.clear();
            amountOfOrganizations.clear();
            amountOfLocations.clear();
            amountOfOthers.clear();
            for (int j = 0; j < results[i].length; ++j) {
                if ((metadata[j] != null) && (results[i][j] >= 0)) {
                    annotatorResults.add(results[i][j]);
                    numberOfDocuments.add(metadata[j].numberOfDocuments);
                    avgDocumentLength.add(metadata[j].avgDocumentLength);
                    numberOfEntities.add(metadata[j].numberOfEntities);
                    entitiesPerDoc.add(metadata[j].entitiesPerDoc);
                    entitiesPerToken.add(metadata[j].entitiesPerToken);
                    amountOfPersons.add(metadata[j].amountOfPersons);
                    amountOfOrganizations.add(metadata[j].amountOfOrganizations);
                    amountOfLocations.add(metadata[j].amountOfLocations);
                    amountOfOthers.add(metadata[j].amountOfOthers);
                }
            }
            // If we have enough datasets with metadata and results of the
            // current annotator for these datasets
            elementCount = annotatorResults.size();
            if (elementCount > MIN_NUMBER_OF_VALUES_FOR_CORR_CALC) {
                annotatorResultsAsArray = annotatorResults.toArray(new double[elementCount]);
                correlations[i][0] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, numberOfDocuments.toArray(new double[elementCount]));
                correlations[i][1] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, avgDocumentLength.toArray(new double[elementCount]));
                correlations[i][2] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, numberOfEntities.toArray(new double[elementCount]));
                correlations[i][3] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, entitiesPerDoc.toArray(new double[elementCount]));
                correlations[i][4] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, entitiesPerToken.toArray(new double[elementCount]));
                correlations[i][5] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, amountOfPersons.toArray(new double[elementCount]));
                correlations[i][6] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, amountOfOrganizations.toArray(new double[elementCount]));
                correlations[i][7] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, amountOfLocations.toArray(new double[elementCount]));
                correlations[i][8] = PearsonsSampleCorrelationCoefficient.calculateRankCorrelation(
                        annotatorResultsAsArray, amountOfOthers.toArray(new double[elementCount]));
                // correlations[i][9] = annotatorResultsAsArray.length;
            }
        }

        return correlations;
    }

    private String generateJson(double[][] results, double[][] correlations, String annotatorNames[],
            String datasetNames[], String[] languages, String leaderBoard) {
        StringBuilder jsonBuilder = new StringBuilder();
        // jsonBuilder.append("results=");
        jsonBuilder.append("{ \"table\": [");
        jsonBuilder.append(generateJSonTableString(results, datasetNames, annotatorNames, "Micro F1-measure"));
        jsonBuilder.append(',');
        jsonBuilder.append(generateJSonTableString(correlations, CORRELATION_TABLE_COLUMN_HEADINGS, annotatorNames,
                "Correlations"));
        jsonBuilder.append("], \"leaderboard\" : ").append(leaderBoard);
        jsonBuilder.append('}');
        return jsonBuilder.toString();
    }

    private String generateJSonTableString(double values[][], String columnHeadings[], String lineHeadings[],
            String tableName) {
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("[[\"");
        dataBuilder.append(tableName);
        for (int i = 0; i < columnHeadings.length; ++i) {
            dataBuilder.append("\",\"");
            dataBuilder.append(columnHeadings[i]);
        }
        for (int i = 0; i < lineHeadings.length; ++i) {
            dataBuilder.append("\"],\n[\"");
            dataBuilder.append(lineHeadings[i]);
            for (int j = 0; j < columnHeadings.length; ++j) {
                dataBuilder.append("\",\"");
                // if this is a real result
                if (values[i][j] > NOT_AVAILABLE_SENTINAL) {
                    dataBuilder.append(String.format(Locale.US, "%.3f", values[i][j]));
                } else {
                    // if this value is simply missing
                    if (values[i][j] == NOT_AVAILABLE_SENTINAL) {
                        dataBuilder.append("n.a.");
                    } else {
                        // this is an error value
                        dataBuilder.append("error (");
                        dataBuilder.append((int) values[i][j]);
                        dataBuilder.append(')');
                    }
                }
            }
        }
        dataBuilder.append("\"]]");
        return dataBuilder.toString();
    }
}
