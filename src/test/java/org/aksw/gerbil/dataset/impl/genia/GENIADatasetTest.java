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
package org.aksw.gerbil.dataset.impl.genia;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class GENIADatasetTest {

	private static final String DATASET_FILE = "src/test/resources/datasets/genia/GENIAcorpus3.02.merged_test.xml";
	private static final String DATASET_NAME = "testDataset";
	private static final List<Document> EXPECTED_DOCUMENTS = Arrays.asList(
			new DocumentImpl("IL-2 gene expression and NF-kappa B activation through CD28 requires reactive oxygen production by 5-lipoxygenase . ",
					"http://" + DATASET_NAME + "/0",
					Arrays.asList(
							new NamedEntity(0, 9, ""),
							new NamedEntity(0, 20, ""),
							new NamedEntity(25, 10, ""),
							new NamedEntity(25, 21, ""),
							new NamedEntity(55, 4, ""),
							new NamedEntity(99, 14, ""))),
			new DocumentImpl("Antigen complexed with major histocompatibility complex class I or II molecules on the surface of antigen presenting cells interacts with the T cell receptor ( TCR ) on the surface of T cells and initiates an activation cascade . ",
					"http://" + DATASET_NAME + "/1",
					Arrays.asList(
							new NamedEntity(23, 56, ""),
							new NamedEntity(98, 24, ""),
							new NamedEntity(142, 15, ""),
							new NamedEntity(160, 3, ""),
							new NamedEntity(184, 7, ""))),
			new DocumentImpl("These results imply that membrane potential changes secondary to the ligand-dependent opening of Ca(2+)-activated K+ channels are not involved in B and T lymphocyte activation and mitogenesis . ",
					"http://" + DATASET_NAME + "/2",
					Arrays.asList(
							new NamedEntity(25, 18, ""),
							new NamedEntity(25, 26, ""),
							new NamedEntity(97, 28, ""),
							new NamedEntity(146, 18, ""),
							new NamedEntity(146, 45, ""))),
			new DocumentImpl("Interestingly , GATA-2 and GATA-3 proteins also localized to the same nuclear bodies in cell lines co-expressing GATA -1 and -2 or GATA -1 and -3 gene products . ",
					"http://" + DATASET_NAME + "/3",
					Arrays.asList(
							new NamedEntity(16, 6, ""),
							new NamedEntity(27, 6, ""),
							new NamedEntity(27, 15, ""),
							new NamedEntity(70, 14, ""),
							new NamedEntity(113, 14, ""),
							new NamedEntity(131, 14, ""),
							new NamedEntity(113, 46, ""))),
			new DocumentImpl("Nuclear C/EBP beta was also detected in rheumatoid synovial fluid monocytes /macrophages , but not in lymphocytes or neutrophils . ",
					"http://" + DATASET_NAME + "/4",
					Arrays.asList(
							new NamedEntity(8, 10, ""),
							new NamedEntity(40, 88, ""))));

	@Test
	public void test() throws GerbilException {
		GENIADataset dataset = new GENIADataset(DATASET_FILE);
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
