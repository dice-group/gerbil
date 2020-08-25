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
package org.aksw.gerbil.dataset.impl.sec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class SECDatasetTest {

	private static final String DATASET_FILE = "src/test/resources/datasets/sec/sec_test.txt";
	private static final String DATASET_NAME = "testDataset";
	private static final List<Document> EXPECTED_DOCUMENTS = Arrays.asList(
			new DocumentImpl("This LOAN AND SECURITY AGREEMENT dated January 27 , 1999 , between SILICON VALLEY BANK (\" Bank \"), a California The SILICON VALLEY BANK ",
					"http://" + DATASET_NAME + "/0",
					Arrays.asList(
							new TypedNamedEntity(67, 19, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Organisation"))),
							new TypedNamedEntity(90, 4, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Organisation"))),
							new TypedNamedEntity(101, 10, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Place"))),
							new TypedNamedEntity(116, 19, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Organisation"))))),
			new DocumentImpl("Dated March 31 , 2007 Thinkplus Investments Limited ( as the Lender ) ",
					"http://" + DATASET_NAME + "/1",
					Arrays.asList(
							new TypedNamedEntity(22, 29, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Organisation"))),
							new TypedNamedEntity(61, 6, "",
									new HashSet<String>(Arrays.asList("http://dbpedia.org/ontology/Person"))))));

	@Test
	public void test() throws GerbilException {
		SECDataset dataset = new SECDataset(DATASET_FILE);
        try {
			dataset.setName(DATASET_NAME);
			dataset.init();
			int size = EXPECTED_DOCUMENTS.size();
			Assert.assertArrayEquals(EXPECTED_DOCUMENTS.toArray(new Document[size]),
                    dataset.getInstances().toArray(new Document[size]));
        } finally {
            IOUtils.closeQuietly(dataset);
        }
	}
}
