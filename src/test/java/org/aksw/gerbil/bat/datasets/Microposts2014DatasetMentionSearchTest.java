package org.aksw.gerbil.bat.datasets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.bat.datasets.Microposts2014Dataset.Microposts2014Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Microposts2014DatasetMentionSearchTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs
                .add(new Object[] {
                        "NOTW phone hacking",
                        "Rupert #Murdoch, asked who was responsible for #NOTW phone #hacking? 'The people I trusted & maybe the people they trusted'",
                        "#NOTW phone #hacking" });
        testConfigs.add(new Object[] { "Amy Winehouse",
                "#Amy #Winehouse Is #Dead After a Suspected Drug Overdose  http://t.co/9KBWCeN via @YahooNews",
                "#Amy #Winehouse" });
        testConfigs
                .add(new Object[] {
                        "White Sox",
                        "#MLB Live Score Update #White #Sox (4) - #Indians (2) Final Play By Play Click link: http://rotoinfo.com/gameview?310724105",
                        "#White #Sox" });
        return testConfigs;
    }

    private String mention;
    private String tweet;
    private String expectedMention;

    public Microposts2014DatasetMentionSearchTest(String mention, String tweet, String expectedMention) {
        this.mention = mention;
        this.tweet = tweet;
        this.expectedMention = expectedMention;
    }

    @Test
    public void test() {
        Microposts2014Annotation annotation = Microposts2014Dataset.findMentionInsideTweetIgnoringHashes(tweet,
                mention, 0, null);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(expectedMention, annotation.mention);
    }
}
