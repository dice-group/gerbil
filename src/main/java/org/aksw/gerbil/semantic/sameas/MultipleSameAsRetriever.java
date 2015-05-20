package org.aksw.gerbil.semantic.sameas;

import java.util.HashSet;
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
