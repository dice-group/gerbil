package org.aksw.gerbil.annotator.http;

import org.apache.http.client.methods.HttpUriRequest;

public class ObservedHttpRequest {

    public HttpUriRequest request;
    public AbstractHttpBasedAnnotator annotator;

    public ObservedHttpRequest(HttpUriRequest request, AbstractHttpBasedAnnotator annotator) {
        this.request = request;
        this.annotator = annotator;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotator == null) ? 0 : annotator.hashCode());
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
        if (annotator == null) {
            if (other.annotator != null)
                return false;
        } else if (!annotator.equals(other.annotator))
            return false;
        if (request == null) {
            if (other.request != null)
                return false;
        } else if (!request.equals(other.request))
            return false;
        return true;
    }

}
