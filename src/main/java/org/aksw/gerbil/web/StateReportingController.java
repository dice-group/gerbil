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
                resultBuilder.append("<br>\n");
                stackTrace = state.stackTrace;
                for (int i = 0; i < stackTrace.length; ++i) {
                    resultBuilder.append("\t\t");
                    resultBuilder.append(stackTrace[i].toString());
                    resultBuilder.append("<br>\n");
                }
                resultBuilder.append("</p>\n");
            }
        }
        return resultBuilder.toString();
    }
}
