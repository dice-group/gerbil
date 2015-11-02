/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.semantic.sameas;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.http.AbstractHttpRequestEmitter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

public class HTTPBasedSameAsRetriever extends AbstractHttpRequestEmitter implements SameAsRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPBasedSameAsRetriever.class);

    private static final String REQUEST_ACCEPT_HEADER_VALUE = RDFLanguages.RDFXML.getContentType().getContentType();

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        if ((uri == null) || (uri.isEmpty())) {
            return null;
        }
        Model model = null;
        try {
            model = requestModel(uri);
        } catch (org.apache.jena.atlas.web.HttpException e) {
            LOGGER.debug("HTTP Exception while requesting uri \"" + uri + "\". Returning null. Exception: "
                    + e.getMessage());
            return null;
        } catch (org.apache.jena.riot.RiotException e) {
            LOGGER.debug("Riot Exception while parsing requested model of uri \"" + uri
                    + "\". Returning null. Exception: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LOGGER.debug("Exception while requesting uri \"" + uri + "\". Returning null.", e);
            return null;
        }
        if (model == null) {
            return null;
        }
        Set<String> result = new HashSet<String>();
        result.add(uri);
        findLinks(uri, result, model);
        if (result.size() > 1) {
            return result;
        } else {
            return null;
        }
    }

    protected void findLinks(String uri, Set<String> uris, Model model) {
        Resource resource = model.getResource(uri);
        String foundUri;
        if (model.contains(resource, OWL.sameAs)) {
            NodeIterator iterator = model.listObjectsOfProperty(resource, OWL.sameAs);
            while (iterator.hasNext()) {
                foundUri = iterator.next().asResource().getURI();
                if (!uris.contains(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
            }
        }
        if (model.contains(null, OWL.sameAs, resource)) {
            ResIterator iterator = model.listSubjectsWithProperty(OWL.sameAs, resource);
            while (iterator.hasNext()) {
                foundUri = iterator.next().getURI();
                if (!uris.contains(foundUri)) {
                    uris.add(foundUri);
                    findLinks(foundUri, uris, model);
                }
            }
        }
    }

    protected Model requestModel(String uri) {
        HttpGet request = createGetRequest(uri);
        request.addHeader(HttpHeaders.ACCEPT, REQUEST_ACCEPT_HEADER_VALUE);
        request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        Model model = null;
        try {

            try {
                response = client.execute(request);
            } catch (java.net.SocketException e) {
                if (e.getMessage().contains(CONNECTION_ABORT_INDICATING_EXCPETION_MSG)) {
                    LOGGER.error("It seems like requesting the model of \"" + uri
                            + "\" needed too much time and was interrupted. Returning null.");
                    return null;
                } else {
                    LOGGER.error("Exception while sending request to \"" + uri + "\". Returning null.", e);
                    return null;
                }
            } catch (UnknownHostException e) {
                LOGGER.info("Couldn't find host of \"" + uri + "\". Returning null.");
                return null;
            } catch (Exception e) {
                LOGGER.error("Exception while sending request to \"" + uri + "\". Returning null.", e);
                return null;
            }
            StatusLine status = response.getStatusLine();
            if ((status.getStatusCode() < 200) || (status.getStatusCode() >= 300)) {
                LOGGER.warn("Response of \"{}\" has the wrong status ({}). Returning null.", uri, status.toString());
                return null;
            }
            // receive NIF document
            entity = response.getEntity();
            Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if (contentTypeHeader == null) {
                LOGGER.error("The response did not contain a content type header. Returning null.");
                return null;
            }
            ContentType contentType = ContentType.create(contentTypeHeader.getValue());
            Lang language = RDFLanguages.contentTypeToLang(contentType);
            if (language == null) {
                LOGGER.error("Couldn't find an RDF language for the content type header value \"{}\". Returning null.",
                        contentTypeHeader.getValue());
                return null;
            }
            // read response and parse NIF
            try {
                model = ModelFactory.createDefaultModel();
                RDFDataMgr.read(model, entity.getContent(), language);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse the response for the URI \"" + uri + "\". Returning null", e);
            }
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e1) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            closeRequest(request);
        }
        return model;
    }

    @Override
    public void addSameURIs(Set<String> uris) {
        Set<String> temp = new HashSet<String>();
        Set<String> result;
        for (String uri : uris) {
            result = retrieveSameURIs(uri);
            if (result != null) {
                temp.addAll(retrieveSameURIs(uri));
            }
        }
        uris.addAll(temp);
    }

}
