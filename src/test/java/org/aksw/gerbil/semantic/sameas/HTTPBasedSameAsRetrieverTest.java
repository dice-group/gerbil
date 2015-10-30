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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.HTTPBasedSameAsRetriever;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HTTPBasedSameAsRetrieverTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, null });
        testConfigs.add(new Object[] { "http://aksw.org/notInWiki/Peter_Pan", null });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Kaufland",
                Arrays.asList("http://fr.dbpedia.org/resource/Kaufland", "http://de.dbpedia.org/resource/Kaufland",
                        "http://wikidata.dbpedia.org/resource/Q685967", "http://cs.dbpedia.org/resource/Kaufland",
                        "http://nl.dbpedia.org/resource/Kaufland", "http://pl.dbpedia.org/resource/Kaufland",
                        "http://wikidata.org/entity/Q685967", "http://rdf.freebase.com/ns/m.0dwt4w",
                        "http://yago-knowledge.org/resource/Kaufland") });
        // testConfigs.add(new Object[] {
        // "http://dbpedia.org/resource/Malaysia",
        // Arrays.asList("http://de.dbpedia.org/resource/Malaysia") });
        return testConfigs;
    }

    private String uri;
    private Set<String> expectedURIs;

    public HTTPBasedSameAsRetrieverTest(String uri, Collection<String> expectedURIs) {
        this.uri = uri;
        if (expectedURIs != null) {
            this.expectedURIs = new HashSet<String>();
            this.expectedURIs.addAll(expectedURIs);
        }
    }

    @Test
    public void test() {
        HTTPBasedSameAsRetriever retriever = new HTTPBasedSameAsRetriever();
        Set<String> uris = retriever.retrieveSameURIs(uri);
        if (expectedURIs == null) {
            Assert.assertNull(uris);
        } else {
            Assert.assertNotNull(uris);
            Assert.assertTrue(uris.containsAll(expectedURIs));
        }
    }
}
