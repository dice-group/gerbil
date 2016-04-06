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
package org.aksw.gerbil.semantic.sameas.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SingleUriSameAsRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriEncodingHandlingSameAsRetriever implements SingleUriSameAsRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriEncodingHandlingSameAsRetriever.class);

    private static final String CHARSET_NAME = "UTF-8";

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        if (uri == null) {
            return null;
        }
        int startPos = findLastPathSegment(uri);
        if (startPos >= 0) {
            Set<String> uris = new HashSet<String>();
            uris.add(uri);
            if (containsEncodedParts(uri, startPos)) {
                try {
                    uris.add(uri.substring(0, startPos) + URLDecoder.decode(uri.substring(startPos), CHARSET_NAME));
                } catch (Exception e) {
                    LOGGER.error("Exception while trying to decode URI. Returning null.", e);
                    return null;
                }
            } else {
                try {
                    uris.add(uri.substring(0, startPos) + URLEncoder.encode(uri.substring(startPos), CHARSET_NAME));
                } catch (Exception e) {
                    LOGGER.error("Exception while trying to encode URI. Returning null.", e);
                    return null;
                }
            }
            if (uris.size() > 1) {
                return uris;
            }
        }
        return null;
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        return retrieveSameURIs(uri);
    }

    /**
     * Searches for encoded parts in the given URI starting from the given
     * index. An encoded part starts with a '%' followed by two hex characters.
     * 
     * @return true if such a part has been found, else false
     */
    protected static boolean containsEncodedParts(String uri, int startPos) {
        int state = 0;
        char chars[] = uri.toCharArray();
        for (int i = startPos; i < chars.length; ++i) {
            switch (chars[i]) {
            case '%':
                state = 1;
                break;
            case '0': // falls through
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f': {
                if (state > 0) {
                    ++state;
                    if (state == 3) {
                        return true;
                    }
                }
                break;
            }
            default:
                if (state > 0) {
                    state = 0;
                }
                break;
            }
        }
        return false;
    }

    /**
     * Searches the start position of the last part of a URIs path.
     * 
     * @param uri
     * @return
     */
    protected static int findLastPathSegment(String uri) {
        int lastSlash = uri.lastIndexOf('/');
        int lastHash = uri.lastIndexOf('#');
        if ((lastSlash < 0) && (lastHash < 0)) {
            return -1;
        }
        if ((lastSlash < lastHash)) {
            return lastHash + 1;
        } else {
            return lastSlash + 1;
        }
    }
}
