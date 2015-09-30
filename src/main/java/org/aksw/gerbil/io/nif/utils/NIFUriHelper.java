/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif.utils;

import org.aksw.gerbil.transfer.nif.Document;

public class NIFUriHelper {

    /**
     * Transforms the NIF URI
     * <code>http://example.org/document_1#char=0,120</code> into the document
     * URI <code>http://example.org/document_1</code>.
     * 
     * @param nifUri
     *            the URI the document has inside the NIF model
     * @return the documents URI without the character position information
     */
    public static String getDocumentUriFromNifUri(String nifUri) {
        int pos = nifUri.lastIndexOf('#');
        if (pos > 0) {
            return nifUri.substring(0, pos);
        } else {
            return nifUri;
        }
    }

    public static String getNifUri(Document document, int endPosition) {
        return getNifUri(document.getDocumentURI(), 0, endPosition);
    }

    public static String getNifUri(String documentURI, int start, int end) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#char=");
        uriBuilder.append(start);
        uriBuilder.append(',');
        uriBuilder.append(end);
        return uriBuilder.toString();
    }
}
