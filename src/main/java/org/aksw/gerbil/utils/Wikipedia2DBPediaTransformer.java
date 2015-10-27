package org.aksw.gerbil.utils;

public class Wikipedia2DBPediaTransformer {

    /**
     * Transforms the given Wikipedia title into a DBPedia URI.
     * 
     * @param title
     * @return
     */
    public static String getDBPediaUri(String title) {
        return "http://dbpedia.org/resource/" + title.replace(' ', '_');
    }
}
