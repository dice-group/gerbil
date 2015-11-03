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
