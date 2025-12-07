package org.aksw.gerbil.database;

public class PendingExplanationTask {
    public final int taskId;
    public final String url;

    public PendingExplanationTask(int taskId, String url) {
        this.taskId = taskId;
        this.url = url;
    }
}

