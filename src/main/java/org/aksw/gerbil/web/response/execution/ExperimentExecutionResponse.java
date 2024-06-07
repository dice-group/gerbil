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
package org.aksw.gerbil.web.response.execution;

/**
 * Response class representing the outcome of an experiment execution attempt.
 */
public class ExperimentExecutionResponse {

    private String experimentId;
    private final String errorMessage;
    private String detailMessage;

    /**
     * Constructs a new ExperimentExecutionResponse with the specified experiment ID and detailed message.
     * @param experimentId the ID of the experiment
     * @param detailMessage detailed message describing the outcome or errors encountered
     */
    public ExperimentExecutionResponse(String experimentId, String detailMessage) {
        this.errorMessage = "Encountered errors while trying to start all needed tasks. " +
            "Aborting the erroneous tasks and continuing the experiment.";
        this.experimentId = experimentId;
        this.detailMessage = detailMessage;
    }

    /**
     * Constructs a new ExperimentExecutionResponse with the specified detailed message.
     * @param detailMessage detailed message describing the outcome or errors encountered
     */
    public ExperimentExecutionResponse(String detailMessage) {
        this.errorMessage = "Encountered errors while trying to start all needed tasks. " +
            "Aborting the experiment.";
        this.detailMessage = detailMessage;
    }

    /**
     * @return the ID of the experiment.
     */
    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @return the error message associated with this response.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return the detailed message associated with this response.
     */
    public String getDetailMessage() {
        return detailMessage;
    }
}
