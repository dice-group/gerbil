package org.aksw.gerbil.dataset.impl.ritter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;

//TODO set @RunWith, @Parameters, @Test again, as soon dataset is in gerbil_data.zip

//@RunWith(Parameterized.class)
public class RitterDatasetTest {

//    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { "Texans	O\nurged	O\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texas coast", "http://dbpedia.org/ontology/Place"} });
        testConfigs.add(new Object[] { "Texans	B-movie\nurged	O\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/Film"} });
        testConfigs.add(new Object[] { "Texans	B-company\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/Company"} });
        testConfigs.add(new Object[] { "Texans	B-facility\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/Place"} });
        testConfigs.add(new Object[] { "Texans	B-musicartist\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/MusicalArtist"} });
        testConfigs.add(new Object[] { "Texans	B-other\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/Unknown"} });
        testConfigs.add(new Object[] { "Texans	B-person\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/Person"} });
        testConfigs.add(new Object[] { "Texans	B-product\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/product"} });
        testConfigs.add(new Object[] { "Texans	B-sportsteam\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/SportsTeam"} });
        testConfigs.add(new Object[] { "Texans	B-tvshow\nurged	B-PER\nto	O\nflee	O\nas	O\nIke	O\nmenaces	O\ncoast	O\n:	O\nAuthorities	O\nhave	O\nurged	O\nresidents	O\nto	O\nflee	O\nthe	O\nTexas	B-geo-loc\ncoast	I-geo-loc\n,	O\na	O\nURL	O", "Texans urged to flee as Ike menaces coast : Authorities have urged residents to flee the Texas coast , a URL ", new String[]{"Texans", "http://dbpedia.org/ontology/TelevisionShow"} });
        return testConfigs;
    }

    private String text;
    private String[] expectedToken;
    private String tweet;

    public RitterDatasetTest(String text, String tweet, String[] expectedToken) {
        this.text = text;
        this.tweet = tweet;
        this.expectedToken = expectedToken;
    }

//    @Test
    public void test() {
        List<Marking> markings = RitterDataset.findMarkings(text);
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > 0);
        Assert.assertTrue(markings.get(0) instanceof NamedEntity);
        TypedNamedEntity ne = (TypedNamedEntity) markings.get(0);
        ne.getTypes().iterator().next().equals(expectedToken[1]);
        String mention = tweet.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
        Assert.assertEquals(expectedToken[0], mention);
    }

}
