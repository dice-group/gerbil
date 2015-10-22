package org.aksw.gerbil.annotator.http;

import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

public abstract class AbstractHttpBasedAnnotator extends AbstractAnnotator {

    public static final String CONNECTION_ABORT_INDICATING_EXCPETION_MSG = "Software caused connection abort";

    public AbstractHttpBasedAnnotator() {
    }

    public AbstractHttpBasedAnnotator(String name) {
        super(name);
    }

    public void interrupt(HttpUriRequest request) throws UnsupportedOperationException {
        request.abort();
    }

    protected HttpPost createPostRequest(String url) {
        HttpPost request = new HttpPost(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    protected HttpGet createGetRequest(String url) {
        HttpGet request = new HttpGet(url);
        HttpManagement.getInstance().reportStart(this, request);
        return request;
    }

    protected void closeRequest(HttpUriRequest request) {
        HttpManagement.getInstance().reportEnd(this, request);
    }
}
