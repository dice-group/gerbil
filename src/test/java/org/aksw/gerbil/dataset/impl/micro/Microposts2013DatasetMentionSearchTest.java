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
package org.aksw.gerbil.dataset.impl.micro;

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
public class Microposts2013DatasetMentionSearchTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { "PER/Sarkozy;LOC/Iran;",
                "_Mention_ : _HASHTAG_ Sarkozy says _HASHTAG_ shield intended to protect against Iran _URL_ _Mention_  _Mention_  _HASHTAG_  _HASHTAG_ ''",
                "#Sarkozy" });
        testConfigs.add(new Object[] { "PER/Sarkozy;LOC/Iran;",
                "_Mention_ : Sarkozy says _HASHTAG_ shield intended to protect against Iran _URL_ _Mention_  _Mention_  _HASHTAG_  _HASHTAG_ ''",
                "Sarkozy" });
        return testConfigs;
    }

    private String mention;
    private String tweet;
    private String expectedMention;

    public Microposts2013DatasetMentionSearchTest(String mention, String tweet, String expectedMention) {
        this.mention = mention;
        this.tweet = tweet;
        this.expectedMention = expectedMention;
    }

    @Test
    public void test() {
//        String line[] = new String[] { "tweet-ID", "orig tweet text", mention, "mention-URI" };
    	tweet = tweet.replaceAll("_HASHTAG_"+" ", "#");
		tweet = tweet.replaceAll("_HASHTAG_", "#");
        List<Marking> markings = Microposts2013Dataset.findMarkings(mention, tweet);
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > 0);
        Assert.assertTrue(markings.get(0) instanceof NamedEntity);
        NamedEntity ne = (NamedEntity) markings.get(0);
        String mention = tweet.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
        Assert.assertEquals(expectedMention, mention);
    }
}
