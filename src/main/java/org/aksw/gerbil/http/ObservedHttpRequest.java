package org.aksw.gerbil.http;

import org.apache.http.client.methods.HttpUriRequest;

public class ObservedHttpRequest {

    public HttpUriRequest request;
    public HttpRequestEmitter emitter;

    public ObservedHttpRequest(HttpUriRequest request, HttpRequestEmitter emitter) {
        this.request = request;
        this.emitter = emitter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((emitter == null) ? 0 : emitter.hashCode());
        result = prime * result + ((request == null) ? 0 : request.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ObservedHttpRequest other = (ObservedHttpRequest) obj;
        if (emitter == null) {
            if (other.emitter != null)
                return false;
        } else if (!emitter.equals(other.emitter))
            return false;
        if (request == null) {
            if (other.request != null)
                return false;
        } else if (!request.equals(other.request))
            return false;
        return true;
    }

}
