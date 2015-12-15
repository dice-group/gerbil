package org.aksw.gerbil.semantic.sameas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class SameAsRetrievalTest {

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
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Malaysia",
                Arrays.asList("http://de.dbpedia.org/resource/Malaysia") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/People's_Republic_of_China",
                Arrays.asList("http://dbpedia.org/resource/China") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Home_Depot",
                Arrays.asList("http://dbpedia.org/resource/The_Home_Depot") });
        testConfigs.add(new Object[] { "http://en.wikipedia.org/wiki/People's_Republic_of_China",
                Arrays.asList("http://en.wikipedia.org/wiki/China") });
        testConfigs.add(new Object[] { "http://en.wikipedia.org/wiki/\"B\"_Movie",
                Arrays.asList("http://en.wikipedia.org/wiki/B_movie") });
        testConfigs.add(new Object[] { "http://en.dbpedia.org/resource/Berlin",
                Arrays.asList("http://dbpedia.org/resource/Berlin") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Gainesville,_Florida",
                Arrays.asList("http://dbpedia.org/resource/Gainesville%2C_Florida") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Gainesville%2C_Florida",
                Arrays.asList("http://dbpedia.org/resource/Gainesville,_Florida") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Richard_Taylor_(British_politician)",
                Arrays.asList("http://dbpedia.org/resource/Richard_Taylor_%28British_politician%29") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Richard_Taylor_%28British_politician%29",
                Arrays.asList("http://dbpedia.org/resource/Richard_Taylor_(British_politician)") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/National_Public_Radio",
                Arrays.asList("http://dbpedia.org/resource/NPR") });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/NPR",
                Arrays.asList("http://dbpedia.org/resource/National_Public_Radio") });
        return testConfigs;
    }

    private String uri;
    private Set<String> expectedURIs;

    public SameAsRetrievalTest(String uri, Collection<String> expectedURIs) {
        this.uri = uri;
        if (expectedURIs != null) {
            this.expectedURIs = new HashSet<String>();
            this.expectedURIs.addAll(expectedURIs);
        }
    }

    @Test
    public void test() {
        SameAsRetriever retriever = SameAsRetrieverSingleton4Tests.getInstance();
        Set<String> uris = retriever.retrieveSameURIs(uri);
        if (expectedURIs == null) {
            Assert.assertNull(uris);
        } else {
            Assert.assertNotNull(uris);
            Assert.assertTrue(uris.toString() + " does not contain all of " + expectedURIs.toString(),
                    uris.containsAll(expectedURIs));
        }
    }

}
