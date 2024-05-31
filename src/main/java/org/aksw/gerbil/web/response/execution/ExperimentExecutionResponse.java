package org.aksw.gerbil.web.response.execution;

public class ExperimentExecutionResponse {

    private String experimentId;
    private final String errorMessage;
    private String detailMessage;

    public ExperimentExecutionResponse(String experimentId, String detailMessage) {
        this.errorMessage = "Encountered errors while trying to start all needed tasks. " +
            "Aborting the erroneous tasks and continuing the experiment.";
        this.experimentId = experimentId;
        this.detailMessage = detailMessage;
    }

    public ExperimentExecutionResponse(String detailMessage) {
        this.errorMessage = "Encountered errors while trying to start all needed tasks. " +
            "Aborting the experiment.";
        this.detailMessage = detailMessage;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
