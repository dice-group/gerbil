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
package org.aksw.gerbil.dataset.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.semantic.kb.SimpleWhiteListBasedUriKBClassifier;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FilteringWhileLoadingTest {

    public static final String[] KNOWN_KBS = new String[] {"http://dbpedia.org", "http://en.dbpedia.org/" };

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, false });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/China", true });
        testConfigs.add(new Object[] { "http://en.dbpedia.org/resource/China", true });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/People's_Republic_of_China", true });
        testConfigs.add(new Object[] { "http://notExisting.wikipedia.org/wiki/China", false });
        testConfigs.add(new Object[] { "http://aksw.org/fake/de.dbpedia.org/resource/China", false });
        return testConfigs;
    }

    private String uri;
    private boolean expectedInKb;

    public FilteringWhileLoadingTest(String uri, boolean expectedInKb) {
        this.uri = uri;
        this.expectedInKb = expectedInKb;
    }

    @Test
    public void run() {
        SimpleWhiteListBasedUriKBClassifier classifier = new SimpleWhiteListBasedUriKBClassifier(KNOWN_KBS);
        
        Meaning meaning = new Annotation(uri);
        // check whether the set contains URIs of the KB
        Assert.assertEquals(expectedInKb, classifier.containsKBUri(meaning.getUris()));
    }
}
