/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.utils;

import java.util.HashSet;
import java.util.Set;

public class WikipediaHelper {

    private static final String WIKIPEPIA_URI_PROTOCOL_PART = "http://";
    private static final String WIKIPEPIA_URI_WIKI_PART = "/wiki/";

    /**
     * Extracts the Wikipedia article title from a given Wikpedia URI. Note that
     * this method expects that the title is preceded by
     * {@link #WIKIPEPIA_URI_WIKI_PART} = {@value #WIKIPEPIA_URI_WIKI_PART}.
     * Note that if there is a query or an ancher inside the URI they won't be
     * removed from the title.
     * 
     * @param uri
     *            the Wikipedia URI
     * @return the Wikipedia article title or null if it couldn't be found.
     */
    public static String getWikipediaTitle(String uri) {
        if (uri == null) {
            return null;
        }
        int startPos = uri.indexOf(WIKIPEPIA_URI_WIKI_PART);
        if (startPos < 0) {
            return null;
        } else {
            startPos += WIKIPEPIA_URI_WIKI_PART.length();
            return uri.substring(startPos).replace('_', ' ');
        }
    }

    /**
     * Generates the URI for a Wikipedia entity with the given domain and the
     * given title.
     * 
     * @param domain
     *            the domain of the Wikipedia
     * @param title
     *            the title of the Wikipedia article
     * @return the URI of the Wikipedia entity or null if one of the two
     *         parameters is null.
     */
    public static String getWikipediaUri(String domain, String title) {
        if ((domain == null) || (title == null)) {
            return null;
        }
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(WIKIPEPIA_URI_PROTOCOL_PART);
        uriBuilder.append(domain);
        uriBuilder.append(WIKIPEPIA_URI_WIKI_PART);
        uriBuilder.append(title.replace(' ', '_'));
        return uriBuilder.toString();
    }

    /**
     * Transforms the given Wikipedia title into a DBpedia URI. Not that this
     * method always translates to the English DBpedia.
     * 
     * @param title
     * @return a DBpedia URI or null if the input title is empty or null
     */
    public static String getDBPediaUri(String title) {
        if ((title != null) && (!title.isEmpty())) {
            return "http://dbpedia.org/resource/" + title.replace(' ', '_');
        } else {
            return null;
        }
    }

    /**
     * Transforms the given Wikipedia title into a DBpedia URI.
     * 
     * @param title
     * @return a Set containing a DBpedia URI or null if the input title is
     *         empty or null
     */
    public static Set<String> generateUriSet(String title) {
        Set<String> uris = new HashSet<String>();
        String uri = getDBPediaUri(title);
        if (uri != null) {
            uris.add(uri);
        }
        return uris;
    }
}
