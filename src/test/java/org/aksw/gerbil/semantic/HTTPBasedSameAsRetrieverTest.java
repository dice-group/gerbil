package org.aksw.gerbil.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

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
        testConfigs.add(new Object[] {
                "http://dbpedia.org/resource/Kaufland",
                Arrays.asList("http://fr.dbpedia.org/resource/Kaufland", "http://de.dbpedia.org/resource/Kaufland",
                        "http://wikidata.dbpedia.org/resource/Q685967", "http://cs.dbpedia.org/resource/Kaufland",
                        "http://nl.dbpedia.org/resource/Kaufland", "http://pl.dbpedia.org/resource/Kaufland",
                        "http://wikidata.org/entity/Q685967", "http://rdf.freebase.com/ns/m.0dwt4w",
                        "http://yago-knowledge.org/resource/Kaufland", "http://bg.dbpedia.org/resource/Кауфланд",
                        "http://ro.dbpedia.org/resource/Kaufland", "http://sk.dbpedia.org/resource/Kaufland",
                        "http://tr.dbpedia.org/resource/Kaufland") });
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
