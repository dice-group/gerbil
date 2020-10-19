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
package org.aksw.gerbil.dataset.impl.ncbi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class NCBIDatasetTest {

	private static final String DATASET_FILE = "src/test/resources/datasets/ncbi/ncbi_test.txt";
	private static final String DATASET_NAME = "testDataset";
	private static final Document EXPECTED_DOCUMENT = new DocumentImpl(
			"Hereditary deficiency of C5 in association with discoid lupus erythematosus.\n" +
			"A 29-year-old woman with discoid lupus erythematosus had undetectable classic pathway complement activity. Hypocomplementemia was due to selective deficiency of C5. One of her children was also deficient. To our knowledge this is the first documented case of an association between discoid lupus erythematosus and C5 deficiency.. ",
			"http://" + DATASET_NAME + "/1999552",
			Arrays.asList(
				(Marking) new NamedEntity(11, 16,
					new HashSet<String>(Arrays.asList("http://omim.org/entry/609536"))),
				(Marking) new NamedEntity(48, 27,
					new HashSet<String>(Arrays.asList("http://id.nlm.nih.gov/mesh/D008179"))),
				(Marking) new NamedEntity(102, 27,
					new HashSet<String>(Arrays.asList("http://id.nlm.nih.gov/mesh/D008179", "http://id.nlm.nih.gov/mesh/D008179"))),
				(Marking) new NamedEntity(184, 18,
					new HashSet<String>(Arrays.asList("http://id.nlm.nih.gov/mesh/D007153"))),
				(Marking) new NamedEntity(224, 16,
					new HashSet<String>(Arrays.asList("http://omim.org/entry/609536"))),
				(Marking) new NamedEntity(359, 27,
					new HashSet<String>(Arrays.asList("http://id.nlm.nih.gov/mesh/D008179"))),
				(Marking) new NamedEntity(391, 13, 
					new HashSet<String>(Arrays.asList("http://omim.org/entry/609536")))));

	@Test
	public void test() throws GerbilException {
		NCBIDataset dataset = new NCBIDataset(DATASET_FILE);
		try {
			dataset.setName(DATASET_NAME);
			dataset.init();
			List<Document> documents = dataset.getInstances();
			Assert.assertEquals(1, documents.size());
			Assert.assertEquals(EXPECTED_DOCUMENT, documents.get(0));
		} finally {
			IOUtils.closeQuietly(dataset);
		}
	}
}
