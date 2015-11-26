package org.aksw.gerbil.semantic.sameas.impl;

/**
 * Very simple approach to find the domain inside a given URI.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class SimpleDomainExtractor {

    private static final String DOMAIN_PREFIX = "://";
    private static final int DOMAIN_PREFIX_LENGTH = DOMAIN_PREFIX.length();

    public static String extractDomain(String uri) {
        if (uri == null) {
            return null;
        }
        // get the start position of the domain
        int startPos = uri.indexOf(DOMAIN_PREFIX);
        if (startPos < 0) {
            startPos = 0;
        } else {
            startPos += DOMAIN_PREFIX_LENGTH;
        }
        // find the end position of the String
        char chars[] = uri.toCharArray();
        for (int i = startPos; i < chars.length; ++i) {
            switch (chars[i]) {
            // if this is a character that is not part of the domain, anymore
            case '/':
            case ':': {
                return uri.substring(startPos, i);
            }
            default: {
                // nothing to do
            }
            }
        }
        // we couldn't find the end, but maybe we have found a start
        return uri.substring(startPos);
    }
}
