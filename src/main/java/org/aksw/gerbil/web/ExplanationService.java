package org.aksw.gerbil.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.AggregatedContingencyMetricsReport;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.ExtendedContingencyMetrics;
import org.aksw.gerbil.evaluate.ObjectEvaluationResult;
import org.aksw.gerbil.evaluate.impl.FMeasureCalculator;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class ExplanationService implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplanationService.class);

    public static final String EXPLANATION_URL_NAME = "explanation url";
    public static final String HUMAN_READABLE_EXPLANATION_NAME = "explanation";
    public static final String MACHINE_READABLE_EXPLANATION_NAME = "machine-readable explanation";

    protected static final String GET_EXPLANATION_URL = "org.aksw.gerbil.explanation.service.url";

    protected CloseableHttpClient client = HttpClients.createDefault();
    protected String explanationUrl;

    public ExplanationService(String explanationUrl) {
        super();
        this.explanationUrl = explanationUrl;
    }

    public <T extends Marking> ExplanationRequestStep<T> createExplanationRequestStep(String datasetName) {
        return new ExplanationRequestStep<T>(this, datasetName);
    }

    public String requestExplanation(String datasetName, List<ExtendedContingencyMetrics> metricsReport,
            EvaluationResultContainer results) {

        // FIXME Get rid of this ugly workaround and replace it by mapping the datasets
        // to explanation dataset names in the meta data
        String requestDatasetName = mapDatasetName(datasetName);

        List<Integer> positive = new ArrayList<>();
        List<Integer> negative = new ArrayList<>();
        for (int i = 0; i < metricsReport.size(); i++) {
            if (metricsReport.get(i).getF1Score() >= 0.5) {
                positive.add(i);
            } else {
                negative.add(i);
            }
        }

        String explanationURL = null;
        try {
            explanationURL = sendF1ScoreData(requestDatasetName, positive, negative);
            if (explanationURL != null) {
                results.addResult(new ObjectEvaluationResult(EXPLANATION_URL_NAME, explanationURL));
                LOGGER.info("Received explanationURL \"{}\" for results on dataset \"{}\"", explanationURL,
                        requestDatasetName);
            } else {
                LOGGER.info("Received no explanationURL for results on dataset \"{}\" (requested \"{}\")", datasetName,
                        requestDatasetName);
            }
        } catch (Exception e) {
            LOGGER.warn("Error sending F1 data: {}", e.getMessage());
        }
        return explanationURL;
    }

    // FIXME Get rid of this ugly workaround and replace it by mapping the datasets
    // to explanation dataset names in the meta data
    private String mapDatasetName(String datasetName) {
        switch (datasetName) {
        case "QALD9 Plus Wikidata":
            return "QALD9_plus_wikidata";
        case "QALD9 Plus DBpedia":
            return "QALD9_plus_dbpedia";
        case "QALD10 Test Multilingual":
            return "QALD10";
        default:
            return datasetName;
        }
    }

    protected String sendF1ScoreData(String dataset, List<Integer> positive, List<Integer> negative)
            throws IOException {
        JsonObject parameters = new JsonObject();
        try {
            parameters.addProperty("dataset", dataset.split("\\.json")[0]);
        } catch (Exception e) {
            parameters.addProperty("dataset", dataset);
        }

        parameters.addProperty("positive", String.join(",", positive.stream().map(String::valueOf).toList()));
        parameters.addProperty("negative", String.join(",", negative.stream().map(String::valueOf).toList()));
        parameters.addProperty("time", 600000);

        HttpPost request = new HttpPost(explanationUrl);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        request.setEntity(new StringEntity(parameters.toString(), StandardCharsets.UTF_8));

        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();
        request.setConfig(config);

        try (CloseableHttpResponse response = client.execute(request);
                InputStream is = response.getEntity().getContent()) {

            int statusCode = response.getStatusLine().getStatusCode();
            if ((statusCode >= 200 && statusCode < 300) || statusCode == 303) {
                return IOUtils.toString(is, StandardCharsets.UTF_8).trim();
            } else {
                LOGGER.error("Received an unexpected response status {}. It will be ignored.", statusCode);
                return null;
            }
        }
    }

    public static class ExplanationRequestStep<T extends Marking> implements Evaluator<T> {

        protected ExplanationService service;
        protected String datasetName;

        public ExplanationRequestStep(ExplanationService service, String datasetName) {
            super();
            this.service = service;
            this.datasetName = datasetName;
        }

        @Override
        public void evaluate(List<Document> instances, List<List<T>> annotatorResults, List<List<T>> goldStandard,
                EvaluationResultContainer results) {
            // Get the metricsReport from previous results
            AggregatedContingencyMetricsReport report = findMatrixResult(results);
            if (report == null) {
                return;
            }
            // call the service
            service.requestExplanation(datasetName, report.getValue(), results);
        }

        protected AggregatedContingencyMetricsReport findMatrixResult(EvaluationResultContainer results) {
            for (EvaluationResult result : results.getResults()) {
                if ((result instanceof AggregatedContingencyMetricsReport)
                        && (result.getName() == FMeasureCalculator.CONTINGENCY_MATRIX_NAME)) {
                    return (AggregatedContingencyMetricsReport) result;
                }
            }
            LOGGER.warn(
                    "Couldn't find an instance of AggregatedContingencyMetricsReport. I won't request an explanation.");
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
        }
    }

}
