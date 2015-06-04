/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.dataset.impl.nif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.dataset.impl.micro.Microposts2014Dataset;
import org.aksw.gerbil.dataset.impl.micro.Microposts2014Dataset.Microposts2014Annotation;
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
