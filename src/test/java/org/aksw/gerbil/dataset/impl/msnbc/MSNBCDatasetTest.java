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
package org.aksw.gerbil.dataset.impl.msnbc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class MSNBCDatasetTest {

    /*
     * The test file cotains two special cases. One marking with the URI
     * "*null*" and two markings that are overlapping.
     */
    private static final String TEST_ANNOTATION_DIR = "src/test/resources/datasets/msnbc/annot";
    private static final String TEST_TEXT_DIR = "src/test/resources/datasets/msnbc/texts";
    private static final String DATASET_NAME = "testDataset";

    private static final String EXPECTED_DOCUMENT_URI = "http://testDataset/test.txt";
    private static final String EXPECTED_TEXT = "Home Depot CEO Nardelli quits \nHome-improvement retailer's chief executive had been criticized over pay \n \nATLANTA - Bob Nardelli abruptly resigned Wednesday as chairman and chief executive of The Home Depot Inc. after a six-year tenure that saw the world’s largest home improvement store chain post big profits but left investors disheartened by poor stock performance.";
    private static final Marking EXPECTED_MARKINGS[] = new Marking[] {
            (Marking) new NamedEntity(0, 10,
                    new HashSet<String>(Arrays.asList("http://en.wikipedia.org/wiki/Home_Depot",
                            "http://dbpedia.org/resource/Home_Depot"))),
            (Marking) new NamedEntity(11, 3, new HashSet<String>(Arrays.asList("*null*"))),
            (Marking) new NamedEntity(15, 8,
                    new HashSet<String>(Arrays.asList("http://en.wikipedia.org/wiki/Robert_Nardelli",
                            "http://dbpedia.org/resource/Robert_Nardelli"))),
            (Marking) new NamedEntity(107, 7,
                    new HashSet<String>(Arrays.asList("http://en.wikipedia.org/wiki/Atlanta,_Georgia",
                            "http://dbpedia.org/resource/Atlanta,_Georgia"))),
            (Marking) new NamedEntity(117, 12,
                    new HashSet<String>(Arrays.asList("http://en.wikipedia.org/wiki/Robert_Nardelli",
                            "http://dbpedia.org/resource/Robert_Nardelli"))),
            (Marking) new NamedEntity(193, 19, new HashSet<String>(Arrays
                    .asList("http://en.wikipedia.org/wiki/Home_Depot", "http://dbpedia.org/resource/Home_Depot"))) };

    @Test
    public void test() throws GerbilException {
        MSNBCDataset dataset = new MSNBCDataset(TEST_TEXT_DIR, TEST_ANNOTATION_DIR);
        dataset.setName(DATASET_NAME);
        dataset.init();
        Assert.assertEquals(1, dataset.getInstances().size());
        Document document = dataset.getInstances().get(0);

        Assert.assertEquals(EXPECTED_DOCUMENT_URI, document.getDocumentURI());
        Assert.assertEquals(EXPECTED_TEXT, document.getText());

        Set<Marking> expectedNEs = new HashSet<Marking>(Arrays.asList(EXPECTED_MARKINGS));
        for (Marking marking : document.getMarkings()) {
            Assert.assertTrue("Couldn't find " + marking.toString() + " inside " + expectedNEs.toString(),
                    expectedNEs.contains(marking));
        }
        Assert.assertEquals(expectedNEs.size(), document.getMarkings().size());
        IOUtils.closeQuietly(dataset);
    }
}
