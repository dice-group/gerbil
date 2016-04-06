package org.aksw.gerbil.qa.datatypes;

import java.util.Arrays;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;

public class AnswerItemType extends Annotation implements Meaning {

    public AnswerItemType(String uri) {
        super(uri);
    }

    public AnswerItemType(Set<String> uris) {
        super(uris);
        setUris(uris);
    }

    public AnswerItemType(AnswerItemType ait) {
        super(ait.getUris());
    }

    @Override
    public String toString() {
        return "AnswerItemType [uri=" + Arrays.toString(uris.toArray()) + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new AnswerItemType(this);
    }
}
