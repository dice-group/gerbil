package org.aksw.gerbil.semantic.sameas.impl.wiki;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WikipediaXMLParserRedirectTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, null });
        testConfigs.add(new Object[] { "", null });
        testConfigs.add(new Object[] {
                "<api batchcomplete=\"\"><query><pages><page _idx=\"-1\" ns=\"0\" title=\"People's Rep\" missing=\"\" contentmodel=\"wikitext\" pagelanguage=\"en\" pagelanguagehtmlcode=\"en\" pagelanguagedir=\"ltr\"/></pages></query></api>",
                null });
        testConfigs.add(new Object[] {
                "<api batchcomplete=\"\"><query><redirects><r from=\"People's Republic of China\" to=\"China\"/></redirects><pages><page _idx=\"5405\" pageid=\"5405\" ns=\"0\" title=\"China\" contentmodel=\"wikitext\" pagelanguage=\"en\" pagelanguagehtmlcode=\"en\" pagelanguagedir=\"ltr\" touched=\"2015-11-24T12:41:48Z\" lastrevid=\"691978625\" length=\"230235\"/></pages></query></api>",
                "China" });
        return testConfigs;
    }

    private String xmlString;
    private String expectedRedirect;

    public WikipediaXMLParserRedirectTest(String xmlString, String expectedRedirect) {
        this.xmlString = xmlString;
        this.expectedRedirect = expectedRedirect;
    }

    @Test
    public void run() {
        WikipediaXMLParser parser = new WikipediaXMLParser();
        Assert.assertEquals(expectedRedirect, parser.extractRedirect(xmlString));
    }
}
