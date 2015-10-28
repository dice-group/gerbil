package org.aksw.gerbil.dataset.impl.iitb;

public class IITB_Annotation {

    public String documentName;
    public String wikiTitle;
    public int offset = -1;
    public int length = -1;

    public boolean isComplete() {
        return (documentName != null) && (offset >= 0) && (length > 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
        result = prime * result + length;
        result = prime * result + offset;
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
        IITB_Annotation other = (IITB_Annotation) obj;
        if (documentName == null) {
            if (other.documentName != null)
                return false;
        } else if (!documentName.equals(other.documentName))
            return false;
        if (length != other.length)
            return false;
        if (offset != other.offset)
            return false;
        return true;
    }
}
