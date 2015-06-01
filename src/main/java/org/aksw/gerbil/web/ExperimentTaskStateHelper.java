/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
