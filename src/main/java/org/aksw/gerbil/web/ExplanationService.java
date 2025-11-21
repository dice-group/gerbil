package org.aksw.gerbil.web;

import com.google.gson.JsonObject;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.ExtendedContingencyMetrics;
import org.aksw.gerbil.evaluate.ObjectEvaluationResult;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ExplanationService<T extends Marking> implements Evaluator<T> {

    private static final Logger log = LoggerFactory.getLogger(ExplanationService.class);
    private final CloseableHttpClient client = HttpClients.createDefault();
    private static final ExplanationService<?> INSTANCE = new ExplanationService<>();
    private static final String GET_EXPLANATION_URL = "org.aksw.gerbil.explanation.service.url";
    private final String explanationUrl = GerbilConfiguration.getInstance().getString(GET_EXPLANATION_URL);
    private EvaluationResultContainer results = null;

    // public access point
    @SuppressWarnings("unchecked")
    public static <T extends Marking> ExplanationService<T> getInstance() {
        return (ExplanationService<T>) INSTANCE;
    }

    @Override
    public void evaluate(List<Document> instances, List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results) {
        //TODO we cannot call API to get explantion url here, because we do not have "dataset" param here
        this.results = results;
    }


    public String executeFilterF1Data(String experimentId,
                                    String datasetName,
                                    List<ExtendedContingencyMetrics> metricsReport) {

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
            explanationURL = sendF1ScoreData(datasetName, positive, negative, experimentId);
            results.addResult(new ObjectEvaluationResult("ExplanationURL", explanationURL));
        } catch (Exception e) {
             log.warn("Error sending F1 data: {}", e.getMessage());
        }
        return explanationURL;
    }

    private String sendF1ScoreData(String dataset, List<Integer> positive, List<Integer> negative, String experimentId) throws IOException {
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

        RequestConfig config = RequestConfig.custom()
                .setRedirectsEnabled(false)
                .build();
        request.setConfig(config);

        try (CloseableHttpResponse response = client.execute(request);
             InputStream is = response.getEntity().getContent()) {

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 303 || statusCode == 400) {
                return IOUtils.toString(is, StandardCharsets.UTF_8).trim();
            } else {
                return null;
            }
        }
    }
}
