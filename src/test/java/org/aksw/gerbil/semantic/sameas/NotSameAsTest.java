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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.impl.CrawlingSameAsRetrieverDecorator;
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
        /*
         * If the first test case is run, the second test case might fail
         * because http://dbpedia.org/resource/Japan points to
         * http://data.nytimes.com/66220885916538669281 = Armenia (is only a
         * problem in this order and if the file based cache is used which
         * merges the result set from the first test case with the result set of
         * the second test case because of the same nytimes URI
         */
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Japan",
                Arrays.asList("http://dbpedia.org/resource/Armenia") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Armenia",
                Arrays.asList("http://dbpedia.org/resource/Japan") });
        /*
         * the same problem as above but with a different example. Here, Sweden
         * has a link to a wrong nytimes URI
         */
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Sweden",
                Arrays.asList("http://dbpedia.org/resource/Malta") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Malta",
                Arrays.asList("http://dbpedia.org/resource/Sweden") });
        return testConfigs;
    }

    private String uri;
    private Set<String> unexpectedUris;

    public NotSameAsTest(String uri, Collection<String> unexpectedUris) {
        this.uri = uri;
        if (unexpectedUris != null) {
            this.unexpectedUris = new HashSet<String>();
            this.unexpectedUris.addAll(unexpectedUris);
        }
    }

    @Test
    public void test() {
        SameAsRetriever retriever = SameAsRetrieverSingleton4Tests.getInstance();
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

}
