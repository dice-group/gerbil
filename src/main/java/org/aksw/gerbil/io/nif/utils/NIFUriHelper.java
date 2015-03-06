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
