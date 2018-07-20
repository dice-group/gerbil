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

import java.util.List;

import org.aksw.gerbil.database.ExperimentDAO;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;

public class ExperimentTaskStateHelper {

    private static final String TASK_RUNNING_TEXT = "The experiment is still running.";
    private static final String STATE_UNKNOWN_TEXT = "The state of this experiment is unknown.";
    
    
    public static boolean taskFinished(ExperimentTaskStatus result) {
        return result.state == ExperimentDAO.TASK_FINISHED;
    }

    public static String getStateText(ExperimentTaskStatus result) {
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

    public static void setStatusLines(List<ExperimentTaskStatus> results) {
        for (ExperimentTaskStatus result : results) {
            if (!taskFinished(result)) {
                result.stateMsg = getStateText(result);
            }
        }
    }
}
