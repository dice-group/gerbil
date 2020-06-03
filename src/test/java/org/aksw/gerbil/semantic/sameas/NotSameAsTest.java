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
package org.aksw.gerbil.semantic.sameas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.impl.CrawlingSameAsRetrieverDecorator;
import org.aksw.gerbil.semantic.sameas.impl.DomainBasedSameAsRetrieverManager;
import org.aksw.gerbil.semantic.sameas.impl.SimpleDomainExtractor;
import org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever;
import org.aksw.gerbil.test.SameAsRetrieverSingleton4Tests;
import org.aksw.gerbil.web.config.RootConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class tests the same as retrieval as it is defined in the
 * {@link RootConfig} class.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@RunWith(Parameterized.class)
public class NotSameAsTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        testConfigs.add(new Object[] { "http://localhost/resource/Japan",
                Arrays.asList("http://de.localhost/resource/Japan"),
                Arrays.asList("http://dbpedia.org/resource/Armenia") });
        testConfigs.add(new Object[] { "http://localhost/resource/Armenia",
                Arrays.asList("http://de.localhost/resource//Armenia"),
                Arrays.asList("http://dbpedia.org/resource/Japan") });
        testConfigs.add(new Object[] { "http://localhost/resource/Sweden",
                Arrays.asList("http://de.localhost/resource/Sweden"),
                Arrays.asList("http://dbpedia.org/resource/Malta") });
        testConfigs.add(new Object[] { "http://localhost/resource/Malta",
                Arrays.asList("http://de.localhost/resource/Sweden"),
                Arrays.asList("http://dbpedia.org/resource/Sweden") });
        return testConfigs;
    }

    private String uri;
    private Set<String> unexpectedUris;
    private Set<String> expectedUris;

    public NotSameAsTest(String uri, Collection<String> expectedUris, Collection<String> unexpectedUris) {
        this.uri = uri;
        if (unexpectedUris != null) {
            this.unexpectedUris = new HashSet<String>();
            this.unexpectedUris.addAll(unexpectedUris);
        }
        if (expectedUris != null) {
            this.expectedUris = new HashSet<String>();
            this.expectedUris.addAll(expectedUris);
        }
    }

    @Test
    public void test() {
        SameAsRetriever retriever = SameAsRetrieverSingleton4Tests.getInstance();
        HTTPBasedSameAsRetriever sameAsRetrieverMock = mock(HTTPBasedSameAsRetriever.class);
        String domain = SimpleDomainExtractor.extractDomain(uri);
        when(sameAsRetrieverMock.retrieveSameURIs(domain, uri)).thenReturn(expectedUris);
        //register a SameAsRetriever mock for the domain of the current uri
        setMock(retriever, sameAsRetrieverMock, domain);
        setDebugging(retriever, true);
        
        Set<String> uris = retriever.retrieveSameURIs(uri);
        if (unexpectedUris != null) {
            for (String unexpectedUri : unexpectedUris) {
                Assert.assertFalse(uris.toString() + " does contain the unexpected URI " + unexpectedUri,
                        uris.contains(unexpectedUri));
            }
        }
        setDebugging(retriever, false);
    }

    protected void setDebugging(SameAsRetriever retriever, boolean debug) {
        if (retriever instanceof SameAsRetrieverDecorator) {
            setDebugging(((SameAsRetrieverDecorator) retriever).getDecorated(), debug);
        }
        if (retriever instanceof CrawlingSameAsRetrieverDecorator) {
            ((CrawlingSameAsRetrieverDecorator) retriever).setDebugCrawling(debug);
        }
    }

    protected void setMock(SameAsRetriever retriever, HTTPBasedSameAsRetriever mock, String domain) {
        if (retriever instanceof SameAsRetrieverDecorator) {
            setMock(((SameAsRetrieverDecorator) retriever).getDecorated(), mock, domain);
        }
        if (retriever instanceof DomainBasedSameAsRetrieverManager) {
            ((DomainBasedSameAsRetrieverManager) retriever).addDomainSpecificRetriever(domain, mock);
        }
    }

}
