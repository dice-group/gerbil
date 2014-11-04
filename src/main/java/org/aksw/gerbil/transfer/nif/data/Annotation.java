package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Meaning;

/**
 * An Annotation is a meaning which is added to a document.
 * 
 * @author Michael Röder
 * 
 */
public class Annotation implements Meaning {

    protected String uri;

    public Annotation(String uri) {
        this.uri = uri;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
        Annotation other = (Annotation) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Annotation [uri=" + uri + "]";
    }

}
