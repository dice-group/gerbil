package org.aksw.gerbil.database;

public class PendingExplanationTask {
    public final String taskId;
    public final String url;

    public PendingExplanationTask(String taskId, String url) {
        this.taskId = taskId;
        this.url = url;
    }
}

