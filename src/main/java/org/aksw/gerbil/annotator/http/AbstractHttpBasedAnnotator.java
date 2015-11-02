package org.aksw.gerbil.annotator.http;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.apache.http.impl.client.CloseableHttpClient;

public abstract class AbstractHttpBasedAnnotator extends AbstractHttpRequestEmitter implements Annotator {

    public AbstractHttpBasedAnnotator() {
        super();
    }

    public AbstractHttpBasedAnnotator(CloseableHttpClient client) {
        super(client);
    }

    public AbstractHttpBasedAnnotator(String name) {
        super(name);
    }

    public AbstractHttpBasedAnnotator(String name, CloseableHttpClient client) {
        super(name, client);
    }
}
