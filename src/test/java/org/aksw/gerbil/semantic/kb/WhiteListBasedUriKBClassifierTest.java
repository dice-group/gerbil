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
package org.aksw.gerbil.semantic.kb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.kb.WhiteListBasedUriKBClassifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WhiteListBasedUriKBClassifierTest {

    private static final String LIST_RESOURCE_NAME = "WhiteListBasedUriKBClassifier_example.txt";

    private static final String EXTENSION_RESULT[] = new String[] { "http://kb1.org/1", "http://kb2.org/1" };

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Arthur_Schopenhauer", true });
        testConfigs.add(new Object[] { "http://de.dbpedia.org/resource/Arthur_Schopenhauer", false });
        testConfigs.add(new Object[] { "http://yago-knowledge.org/resource/wordnet_watercolor_100938642", true });
        return testConfigs;
    }

    private String uri;
    private boolean expectedResult;

    public WhiteListBasedUriKBClassifierTest(String uri, boolean expectedResult) {
        this.uri = uri;
        this.expectedResult = expectedResult;
    }

    @Test
    public void testSingle() {
        WhiteListBasedUriKBClassifier classifier = WhiteListBasedUriKBClassifier.create(this.getClass()
                .getClassLoader().getResourceAsStream(LIST_RESOURCE_NAME));
        Assert.assertNotNull(classifier);
        Assert.assertEquals(expectedResult, classifier.isKBUri(uri));
    }

    @Test
    public void testSet() {
        WhiteListBasedUriKBClassifier classifier = WhiteListBasedUriKBClassifier.create(this.getClass()
                .getClassLoader().getResourceAsStream(LIST_RESOURCE_NAME));
        Assert.assertNotNull(classifier);

        Set<String> set = new HashSet<String>();
        set.add(uri);
        for (int i = 0; i < EXTENSION_RESULT.length; ++i) {
            set.add(EXTENSION_RESULT[i]);
        }
        Assert.assertEquals(expectedResult, classifier.containsKBUri(set));
    }
}
