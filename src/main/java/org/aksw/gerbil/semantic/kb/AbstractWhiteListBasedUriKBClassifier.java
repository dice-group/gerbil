package org.aksw.gerbil.semantic.kb;

import java.util.Collection;
import java.util.List;

public abstract class AbstractWhiteListBasedUriKBClassifier implements UriKBClassifier {

    protected List<String> kbNamespaces;

    public AbstractWhiteListBasedUriKBClassifier(List<String> kbNamespaces) {
        this.kbNamespaces = kbNamespaces;
    }

    @Override
    public boolean isKBUri(String uri) {
        for (String namespace : kbNamespaces) {
            if (uri.startsWith(namespace)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKBUri(Collection<String> uris) {
        for (String uri : uris) {
            if (isKBUri(uri)) {
                return true;
            }
        }
        return false;
    }

}
