package org.aksw.gerbil.semantic;

public interface SameAsRetrieverDecorator extends SameAsRetriever {

    public SameAsRetriever getDecorated();
}
