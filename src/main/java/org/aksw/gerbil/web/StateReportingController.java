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

import org.aksw.simba.topicmodeling.concurrent.overseers.Overseer;
import org.aksw.simba.topicmodeling.concurrent.tasks.TaskState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StateReportingController {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(StateReportingController.class);

    @Autowired
    private List<Overseer> overseers;

    @RequestMapping("/running")
    public @ResponseBody
    String running() {
        StringBuilder resultBuilder = new StringBuilder();
        TaskState states[];
        StackTraceElement stackTrace[];
        for (Overseer overseer : overseers) {
            states = overseer.getTaskStates();
            for (TaskState state : states) {
                resultBuilder.append("<p>");
                resultBuilder.append(state.task.getId());
                resultBuilder.append("<br>\n");
                resultBuilder.append("state=");
                resultBuilder.append(state.state);
                resultBuilder.append("<br>\n");
                resultBuilder.append("progress=");
                resultBuilder.append(state.task.getProgress());
                stackTrace = state.stackTrace;
                if (state.stackTrace != null) {
                    resultBuilder.append("<br>\n");
                    for (int i = 0; i < stackTrace.length; ++i) {
                        resultBuilder.append("\t\t");
                        resultBuilder.append(stackTrace[i].toString());
                        resultBuilder.append("<br>\n");
                    }
                }
                resultBuilder.append("</p>\n");
            }
        }
        return resultBuilder.toString();
    }
}
