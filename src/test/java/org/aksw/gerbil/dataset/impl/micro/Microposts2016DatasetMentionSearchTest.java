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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Microposts2016DatasetMentionSearchTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { 
        		new String[] { "375737278276321000	27	33	http://dbpedia.org/resource/Africa	1	Location", "375737278276321000	4	6	http://dbpedia.org/resource/United_States	1	Location"},
                "The US Military’s Pivot to Africa http://t.co/wJn6YRE5hR",
                new String[] {"US", "Africa" },
                new String[] {"http://dbpedia.org/ontology/Place", "http://dbpedia.org/ontology/Place" }});
       
        testConfigs.add(new Object[] { 
        		new String[] { "375733981582729000	38	46	http://dbpedia.org/resource/Brooklyn	1	Location", 
        				"375733981582729000	4	15	NIL840	1	Product"},
                "RT @BestProNews: A 19-year-old man in Brooklyn died today after he lost control of his remote control helicopter and sliced off the top of …",
                new String[] {"BestProNews", "Brooklyn"},
                new String[] {"http://dbpedia.org/ontology/Product", "http://dbpedia.org/ontology/Place" }});

        
        return testConfigs;
    }

    private String[] mentions;
    private String tweet;
    private String[] expectedMentions;
    private String[] expectedTypes;
    

    public Microposts2016DatasetMentionSearchTest(String[] mentions, String tweet, String[] expectedMentions, String[] expectedTypes) {
        this.mentions = mentions;
        this.tweet = tweet;
        this.expectedMentions = expectedMentions;
        this.expectedTypes = expectedTypes;
    }

    @Test
    public void test() {
        Set<String> lines = new HashSet<String>();
        for(String m : mentions){
        	lines.add(m);
        }
        List<Marking> markings = Microposts2016Dataset.findMarkings(lines, tweet); 
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > 0);
        int i =0;
        for(Marking marking : markings){
        	Assert.assertTrue(marking instanceof TypedNamedEntity);
        	TypedNamedEntity ne = (TypedNamedEntity) marking;
        	
        	String mention = tweet.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
        	Assert.assertEquals(expectedMentions[i], mention);
        	
        	String type = ne.getTypes().iterator().next();
        	Assert.assertEquals(expectedTypes[i], type);
        	i++;
        }
    }
}
