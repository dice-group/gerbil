package org.aksw.gerbil.semantic.sameas;

public interface SameAsRetrieverDecorator extends SameAsRetriever {

    public SameAsRetriever getDecorated();
}
