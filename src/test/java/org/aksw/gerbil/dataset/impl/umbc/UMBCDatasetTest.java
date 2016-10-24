package org.aksw.gerbil.dataset.impl.umbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UMBCDatasetTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { "Texans	O\nurged	O\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-LOC\ncoast	I-LOC\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", "Texas coast" });
        testConfigs.add(new Object[] { "Texans	B-LOC\nurged	O\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-LOC\ncoast	I-LOC\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", "Texans" });
        testConfigs.add(new Object[] { "Texans	B-LOC\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-LOC\ncoast	I-LOC\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", "Texans" });

        return testConfigs;
    }

    private String text;
    private String expectedToken;
    private String tweet;

    public UMBCDatasetTest(String text, String tweet, String expectedToken) {
        this.text = text;
        this.tweet = tweet;
        this.expectedToken = expectedToken;
    }

    @Test
    public void test() {
        List<Marking> markings = UMBCDataset.findMarkings(text);
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > 0);
        Assert.assertTrue(markings.get(0) instanceof NamedEntity);
        NamedEntity ne = (NamedEntity) markings.get(0);
        String mention = tweet.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
        Assert.assertEquals(expectedToken, mention);
    }

}
