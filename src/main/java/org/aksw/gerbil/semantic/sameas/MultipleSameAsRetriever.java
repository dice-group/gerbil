package org.aksw.gerbil.semantic.sameas;

import java.util.Set;

public class MultipleSameAsRetriever implements SameAsRetriever {

    private SameAsRetriever retriever[];

    public MultipleSameAsRetriever(SameAsRetriever... retriever) {
        this.retriever = retriever;
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        Set<String> result = null, newResult = null;
        for (int i = 0; i < retriever.length; ++i) {
            newResult = retriever[i].retrieveSameURIs(uri);
            if (newResult != null) {
                if (result != null) {
                    result.addAll(newResult);
                } else {
                    result = newResult;
                }
            }
        }
        return result;
    }

}
