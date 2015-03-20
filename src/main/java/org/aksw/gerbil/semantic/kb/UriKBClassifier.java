package org.aksw.gerbil.semantic.kb;

import java.util.Collection;

public interface UriKBClassifier {

    public boolean isKBUri(String uri);

    public boolean containsKBUri(Collection<String> uris);
}
