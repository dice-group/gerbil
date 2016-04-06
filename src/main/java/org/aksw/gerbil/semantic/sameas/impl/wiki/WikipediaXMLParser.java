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
package org.aksw.gerbil.semantic.sameas.impl.wiki;

import org.apache.commons.lang3.StringEscapeUtils;

public class WikipediaXMLParser {

    private static final String XML_REDIRECTS_START_TAG = "<redirects>";
    private static final String XML_REDIRECTS_END_TAG = "</redirects>";
    private static final String XML_REDIRECT_TAG_START = "<r";
    private static final char XML_REDIRECT_TAG_END = '>';
    private static final String XML_REDIRECT_TO_ATTRIBUTE_START = "to=\"";
    private static final char XML_REDIRECT_TO_ATTRIBUTE_END = '"';

    /**
     * <p>
     * Extracts the value of the <code>to</code> attribute of the first redirect
     * that it can find inside the given XML string.
     * </p>
     * 
     * <p>
     * It is assumed that the given String looks like this: <code>
     * ...
     * &lt;redirects&gt;
     * ...
     * &lt;r from="title" to="redirected title"/&gt;
     * ...
     * &lt;/redirects&gt;
     * ...
     * </code>
     * </p>
     * 
     * @param xmlString
     *            XML string from which the redirect should be parsed.
     * @return The title to which the given title is redirected or null, if such
     *         a title couldn't be found.
     */
    public String extractRedirect(String xmlString) {
        if (xmlString == null) {
            return null;
        }
        int startPos = xmlString.indexOf(XML_REDIRECTS_START_TAG);
        if (startPos < 0) {
            // couldn't find redirects tag
            return null;
        }

        startPos += XML_REDIRECTS_START_TAG.length();
        int redirectsEnd = xmlString.indexOf(XML_REDIRECTS_END_TAG, startPos);
        if (redirectsEnd < 0) {
            // couldn't find redirects end tag (no valid XML)
            return null;
        }

        startPos = xmlString.indexOf(XML_REDIRECT_TAG_START, startPos);
        if ((startPos < 0) || (startPos >= redirectsEnd)) {
            // couldn't find redirect tag
            return null;
        }
        startPos += XML_REDIRECT_TAG_START.length();

        int tagEndPos = xmlString.indexOf(XML_REDIRECT_TAG_END, startPos);
        if ((tagEndPos < 0) || (tagEndPos >= redirectsEnd)) {
            // couldn't find the end of the redirect tag (no valid XML)
            return null;
        }

        startPos = xmlString.indexOf(XML_REDIRECT_TO_ATTRIBUTE_START, startPos);
        if ((startPos < 0) || (startPos >= tagEndPos)) {
            // couldn't find the 'to' attribute
            return null;
        }
        startPos += XML_REDIRECT_TO_ATTRIBUTE_START.length();

        int endPos = xmlString.indexOf(XML_REDIRECT_TO_ATTRIBUTE_END, startPos);
        if ((endPos < 0) || (endPos >= tagEndPos)) {
            // couldn't find the end of the 'to' attribute value (no valid XML)
            return null;
        }
        return StringEscapeUtils.unescapeXml(xmlString.substring(startPos, endPos));
    }

}
