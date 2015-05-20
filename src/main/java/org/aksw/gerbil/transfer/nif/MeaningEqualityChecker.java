package org.aksw.gerbil.transfer.nif;

import java.util.Set;

public class MeaningEqualityChecker {

    public static final boolean overlaps(Meaning m1, Meaning m2) {
        return overlaps(m1.getUris(), m2.getUris());
    }

    public static final boolean overlaps(Set<String> uris1, Set<String> uris2) {
        Set<String> smaller, larger;
        if (uris1.size() > uris2.size()) {
            smaller = uris2;
            larger = uris1;
        } else {
            smaller = uris1;
            larger = uris2;
        }
        for (String uri : smaller) {
            if (larger.contains(uri)) {
                return true;
            }
        }
        return false;
    }

}
