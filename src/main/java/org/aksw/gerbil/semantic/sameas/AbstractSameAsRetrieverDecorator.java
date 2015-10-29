package org.aksw.gerbil.semantic.sameas;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSameAsRetrieverDecorator implements SameAsRetrieverDecorator {

    protected SameAsRetriever decoratedRetriever;

    public AbstractSameAsRetrieverDecorator(SameAsRetriever decoratedRetriever) {
        this.decoratedRetriever = decoratedRetriever;
    }

    @Override
    public SameAsRetriever getDecorated() {
        return decoratedRetriever;
    }

    @Override
    public void addSameURIs(Set<String> uris) {
        Set<String> temp = new HashSet<String>();
        Set<String> result;
        for (String uri : uris) {
            result = retrieveSameURIs(uri);
            if (result != null) {
                temp.addAll(retrieveSameURIs(uri));
            }
        }
        uris.addAll(temp);
    }
}
