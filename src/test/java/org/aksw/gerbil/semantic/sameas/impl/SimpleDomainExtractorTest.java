package org.aksw.gerbil.semantic.sameas.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SimpleDomainExtractorTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, null });
        testConfigs.add(new Object[] { "http://aksw.org/notInWiki/Peter_Pan", "aksw.org" });
        testConfigs.add(new Object[] { "", "" });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Kaufland", "dbpedia.org" });
        testConfigs.add(new Object[] { "http://fr.dbpedia.org/resource/Kaufland", "fr.dbpedia.org" });
        testConfigs.add(new Object[] { "http://wikidata.dbpedia.org/resource/Q685967", "wikidata.dbpedia.org" });
        testConfigs.add(new Object[] { "http://rdf.freebase.com/ns/m.0dwt4w", "rdf.freebase.com" });
        testConfigs.add(new Object[] { "http://yago-knowledge.org/resource/Kaufland", "yago-knowledge.org" });
        testConfigs.add(new Object[] { "http://139.18.2.164:1235/gerbil", "139.18.2.164" });
        testConfigs.add(new Object[] { "abc", "abc" });
        testConfigs.add(new Object[] { "http://dbpedia.org", "dbpedia.org" });
        testConfigs.add(new Object[] { "aksw.org/notInWiki/Peter_Pan", "aksw.org" });
        return testConfigs;
    }

    private String uri;
    private String expectedDomain;

    public SimpleDomainExtractorTest(String uri, String expectedDomain) {
        this.uri = uri;
        this.expectedDomain = expectedDomain;
    }

    @Test
    public void run() {
        Assert.assertEquals(expectedDomain, SimpleDomainExtractor.extractDomain(uri));
    }
}
