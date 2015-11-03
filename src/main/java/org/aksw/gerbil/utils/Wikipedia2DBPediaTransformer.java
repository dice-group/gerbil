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
