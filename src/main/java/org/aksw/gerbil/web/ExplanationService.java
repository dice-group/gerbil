package org.aksw.gerbil.web;

import com.google.gson.JsonObject;
import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.evaluate.ExtendedContingencyMetrics;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExplanationService {
    private static final CloseableHttpClient client = HttpClients.createDefault();
    private static final Logger log = LoggerFactory.getLogger(ExplanationService.class);
    private static String EXPLANATION_URL = null;
    private static String EXPERIMENT_ID = null;
    ExperimentDAO experimentDAO;

    public void executeFilterF1Data(
            String id,
            String datasetName,
            List<ExtendedContingencyMetrics> metricsReport,
            ExperimentDAO experimentDAO
    ) {
        this.experimentDAO = experimentDAO;
        EXPLANATION_URL = null;
        EXPERIMENT_ID = null;
        String explanationURL = null;
        List<Integer> positive = new ArrayList<>();
        List<Integer> negative = new ArrayList<>();
        for (int i = 0; i < metricsReport.size(); i++) {
            ExtendedContingencyMetrics metric = metricsReport.get(i);
            if (metric.getF1Score() >= 0.5) {
                positive.add(i);
            } else {
                negative.add(i);
            }
        }
        try {
            explanationURL = sendF1ScoreData(datasetName,positive, negative,
                    "http://131.234.28.27:9999/PruneCEL/Parameter",
                    id
            );
        }catch (Exception e){
            log.warn("Something went wrong {}", e.getMessage());
        }finally {
            experimentDAO.saveApiResponse(id,
                    1234321,
                    positive.stream().map(String::valueOf).collect(Collectors.joining(",")),
                    negative.stream().map(String::valueOf).collect(Collectors.joining(",")),
                    explanationURL);
        }
    }

    public String sendF1ScoreData(String dataset, List<Integer> positive, List<Integer> negative, String url, String experimentId) throws IOException {
        JsonObject parameters = new JsonObject();
        try {
            parameters.addProperty("dataset", dataset.split("\\.json")[0]);
        } catch (Exception e) {
            parameters.addProperty("dataset", dataset);
            log.error(e.getMessage());
        }

        parameters.addProperty("positive", positive.stream().map(String::valueOf).collect(Collectors.joining(",")));
        parameters.addProperty("negative", negative.stream().map(String::valueOf).collect(Collectors.joining(",")));
        parameters.addProperty("time", 60000);


        CloseableHttpClient client = HttpClients.custom()
                .disableRedirectHandling()
                .build();

        HttpPost request = new HttpPost("http://131.234.28.27:9999/PruneCEL/Parameter");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        request.setEntity(new StringEntity(parameters.toString(), "UTF8"));

        CloseableHttpResponse response = client.execute(request);
        InputStream is = null;

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200 || statusCode == 303 || statusCode == 400) {
                is = response.getEntity().getContent();
                String rawResponse = IOUtils.toString(is, "UTF-8").trim();
                if (statusCode == 303) {
                    log.info("Received redirect in body (not Location header): {}", rawResponse);
                }
                EXPLANATION_URL = rawResponse;
                EXPERIMENT_ID = experimentId;
                return rawResponse;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            IOUtils.closeQuietly(is);
            EntityUtils.consumeQuietly(response.getEntity());
            response.close();
        }

    }

    @Scheduled(fixedDelay = 20000)
    public void checkStatus() {
        if (EXPLANATION_URL == null) return;

        if(!EXPLANATION_URL.contains("http")) {
            experimentDAO.updateResult(
                    EXPERIMENT_ID,
                    EXPLANATION_URL,
                    "Not available for this dataset",
                    "Not available for this dataset");
            EXPLANATION_URL = null;
            EXPERIMENT_ID = null;
            return;
        }
        HttpGet request = new HttpGet(EXPLANATION_URL);
        request.addHeader("Accept", "application/json");
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (statusCode == 200) {
                Map<String, String> map = (Map<String, String>) JSONValue.parse(responseBody);
                String pruneCELResult = map.getOrDefault("pruneCELResult", "");
                if (pruneCELResult == null) pruneCELResult = "";
                String llmResult = map.getOrDefault("llmResult", "");
                if (llmResult == null) llmResult = "";
                experimentDAO.updateResult(EXPERIMENT_ID,EXPLANATION_URL, pruneCELResult, llmResult);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


