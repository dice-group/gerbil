package org.aksw.gerbil.qa.datatypes;

import java.util.Arrays;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;

public class Property extends Annotation implements Meaning {

    public Property(String uri) {
        super(uri);
    }

    public Property(Set<String> uris) {
        super(uris);
        setUris(uris);
    }

    public Property(Property property) {
        super(property.getUris());
    }

    @Override
    public String toString() {
        return "Property [uri=" + Arrays.toString(uris.toArray()) + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Property(this);
    }
}
