package org.aksw.gerbil.annotator.http;

import java.io.IOException;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.ClosePermitionGranter;
import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.apache.http.impl.client.CloseableHttpClient;

public abstract class AbstractHttpBasedAnnotator extends AbstractHttpRequestEmitter implements Annotator {

    protected ClosePermitionGranter granter;

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

    @Override
    public void setClosePermitionGranter(ClosePermitionGranter granter) {
        this.granter = granter;
    }

    @Override
    public final void close() throws IOException {
        if (granter.givePermissionToClose()) {
            performClose();
        }
    }

    protected void performClose() throws IOException {
        super.close();
    }
}
