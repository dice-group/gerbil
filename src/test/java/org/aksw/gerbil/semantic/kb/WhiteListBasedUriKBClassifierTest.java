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
