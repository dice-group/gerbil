package org.aksw.gerbil.semantic.sameas.impl.wiki;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.aksw.gerbil.semantic.sameas.SingleUriSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.SimpleDomainExtractor;
import org.aksw.gerbil.utils.WikipediaHelper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class WikipediaApiBasedSingleUriSameAsRetriever extends AbstractHttpRequestEmitter
        implements SingleUriSameAsRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikipediaApiBasedSingleUriSameAsRetriever.class);

    private static final String URL_PROTOCOL_PART = "http://";
    private static final String URL_QUERY_PART = "/w/api.php?format=xml&action=query&redirects=true&titles=";
    private static final String CHARSET_NAME = "UTF-8";
    private static final Escaper TITLE_ESCAPER = UrlEscapers.urlFormParameterEscaper();

    private Charset charset;
    private WikipediaXMLParser parser = new WikipediaXMLParser();

    public WikipediaApiBasedSingleUriSameAsRetriever() {
        try {
            charset = Charset.forName(CHARSET_NAME);
        } catch (Exception e) {
            charset = Charset.defaultCharset();
        }
    }

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        return retrieveSameURIs(SimpleDomainExtractor.extractDomain(uri), uri);
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        if ((domain == null) || (uri == null)) {
            return null;
        }
        String title = WikipediaHelper.getWikipediaTitle(uri);
        if (title == null) {
            return null;
        }
        String redirectedTitle = queryRedirect(domain, title);
        if ((redirectedTitle != null) && (!title.equals(redirectedTitle))) {
            Set<String> uris = new HashSet<String>();
            uris.add(uri);
            uris.add(WikipediaHelper.getWikipediaUri(domain, redirectedTitle));
            return uris;
        } else {
            return null;
        }
    }

    public String queryRedirect(String domain, String title) {
        StringBuilder urlBuilder = new StringBuilder(150);
        urlBuilder.append(URL_PROTOCOL_PART);
        urlBuilder.append(domain);
        urlBuilder.append(URL_QUERY_PART);
        urlBuilder.append(TITLE_ESCAPER.escape(title));

        HttpGet request = null;
        try {
            request = createGetRequest(urlBuilder.toString());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Got an exception while creating a request querying the wiki api of \"" + domain
                    + "\". Returning null.", e);
            return null;
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = sendRequest(request);
            entity = response.getEntity();
            return parser.extractRedirect(IOUtils.toString(entity.getContent(), charset));
        } catch (Exception e) {
            LOGGER.error("Got an exception while querying the wiki api of \"" + domain + "\". Returning null.", e);
            return null;
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            IOUtils.closeQuietly(response);
            closeRequest(request);
        }
    }

}
