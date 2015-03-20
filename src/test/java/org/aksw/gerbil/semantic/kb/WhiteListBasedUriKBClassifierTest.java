package org.aksw.gerbil.semantic.kb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.aksw.gerbil.semantic.kb.WhiteListBasedUriKBClassifier;
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
