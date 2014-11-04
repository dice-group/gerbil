package org.aksw.gerbil.transfer.nif.data;

import org.aksw.gerbil.transfer.nif.Meaning;

public class NamedEntity extends SpanImpl implements Meaning {

    protected String uri;

    public NamedEntity(int startPosition, int length, String uri) {
        super(startPosition, length);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedEntity other = (NamedEntity) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + startPosition + ", " + length + ", " + uri + ")";
    }

}
