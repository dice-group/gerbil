package org.aksw.gerbil.http;

import java.io.Closeable;

import org.apache.http.client.methods.HttpUriRequest;

public interface HttpRequestEmitter extends Closeable {

    public void interrupt(HttpUriRequest request) throws UnsupportedOperationException;

    public String getName();
    
    public void setName(String name);
}
