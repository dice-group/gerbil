package org.aksw.gerbil.utils;

import java.util.HashSet;
import java.util.Set;

public class Wikipedia2DBPediaTransformer {

    /**
     * Transforms the given Wikipedia title into a DBPedia URI.
     * 
     * @param title
     * @return an DBPedia URI or null if the input title is empty or null
     */
    public static String getDBPediaUri(String title) {
        if ((title != null) && (!title.isEmpty())) {
            return "http://dbpedia.org/resource/" + title.replace(' ', '_');
        } else {
            return null;
        }
    }

    public static Set<String> generateUriSet(String title) {
        Set<String> uris = new HashSet<String>();
        String uri = getDBPediaUri(title);
        if (uri != null) {
            uris.add(uri);
        }
        return uris;
    }
}
