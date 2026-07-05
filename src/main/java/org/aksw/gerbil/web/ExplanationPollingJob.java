package org.aksw.gerbil.web;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.database.PendingExplanationTask;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExplanationPollingJob {

    private static final Logger log = LoggerFactory.getLogger(ExplanationPollingJob.class);
    private final HttpClient client = HttpClients.createDefault();

    @Autowired
    @Qualifier("experimentDAO")
    private ExperimentDAO experimentDAO;

    @Scheduled(fixedDelay = 30000) // every 30 sec
    public void pollExplanationResults() {
        log.info("Checking explanation status...");
        List<PendingExplanationTask> jobs = experimentDAO.getPendingExplanations();

        for (PendingExplanationTask job : jobs) {
            String url = job.url;
            int taskId = job.taskId;

            if (url == null || !url.startsWith("http")) {
                log.warn("Invalid explanation URL for task {}: {}", taskId, url);
                continue;
            }

            try {
                HttpGet request = new HttpGet(url);
                request.addHeader("Accept", "application/json");

                try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode == 200) {
                        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        @SuppressWarnings("unchecked")
                        Map<String, String> map = (Map<String, String>) JSONValue.parse(responseBody);

                        if (map == null) {
                            log.warn("Received empty or invalid explanation payload for task {}", taskId);
                            continue;
                        }

                        String pruneCELResult = Objects.toString(map.get("pruneCELResult"), "").trim();
                        String llmResult = Objects.toString(map.get("llmResult"), "").trim();

                        experimentDAO.setExplanation(taskId, ExplanationService.MACHINE_READABLE_EXPLANATION_NAME,
                                pruneCELResult);
                        experimentDAO.setExplanation(taskId, ExplanationService.HUMAN_READABLE_EXPLANATION_NAME,
                                llmResult);
                        log.info("Explanation saved for task {} | machine-readable: \"{}\", human-readable: \"{}\"",
                                taskId, pruneCELResult, llmResult);
                    } else {
                        log.warn("Received status {} while polling explanation for task {}", statusCode, taskId);
                    }
                }
            } catch (Exception e) {
                log.error("Error polling explanation for task {}: {}", taskId, e.getMessage());
            }
        }
    }
}
