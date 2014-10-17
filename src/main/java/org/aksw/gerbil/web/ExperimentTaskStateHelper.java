package org.aksw.gerbil.web;

import java.util.List;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;

public class ExperimentTaskStateHelper {

    private static final String TASK_RUNNING_TEXT = "The experiment is still running.";
    private static final String STATE_UNKNOWN_TEXT = "The state of this experiment is unknown.";

    public static boolean taskFinished(ExperimentTaskResult result) {
        return result.state == ExperimentDAO.TASK_FINISHED;
    }

    public static String getStateText(ExperimentTaskResult result) {
        if (result.state == ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET) {
            return TASK_RUNNING_TEXT;
        }
        ErrorTypes errorType = ErrorTypes.getErrorType(result.state);
        if (errorType != null) {
            return errorType.getDescription();
        } else {
            return STATE_UNKNOWN_TEXT;
        }
    }

    public static void setStatusLines(List<ExperimentTaskResult> results) {
        for (ExperimentTaskResult result : results) {
            if (!taskFinished(result)) {
                result.stateMsg = getStateText(result);
            }
        }
    }
}
