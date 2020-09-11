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
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class GENIADatasetTest {

	private static final String DNA = "http://id.nlm.nih.gov/mesh/D004247";
	private static final String RNA = "http://id.nlm.nih.gov/mesh/D012313";
	private static final String PROTEIN = "http://id.nlm.nih.gov/mesh/D011506";
	private static final String CELL_LINE = "http://id.nlm.nih.gov/mesh/D002478";
	private static final String CELL_TYPE = "http://id.nlm.nih.gov/mesh/D002477";

	private static final String DATASET_FILE = "src/test/resources/datasets/genia/GENIAcorpus3.02.merged_test.xml";
	private static final String DATASET_NAME = "testDataset";
	private static final List<Document> EXPECTED_DOCUMENTS = Arrays.asList(
			new DocumentImpl("IL-2 gene expression and NF-kappa B activation through CD28 requires reactive oxygen production by 5-lipoxygenase . ",
					"http://" + DATASET_NAME + "/0",
					Arrays.asList(
							new TypedNamedEntity(0, 9, "", new HashSet<String>(Arrays.asList(DNA))),
							new TypedNamedEntity(25, 10, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(55, 4, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(99, 14, "", new HashSet<String>(Arrays.asList(PROTEIN))))),
			new DocumentImpl("Antigen complexed with major histocompatibility complex class I or II molecules on the surface of antigen presenting cells interacts with the T cell receptor ( TCR ) on the surface of T cells and initiates an activation cascade . ",
					"http://" + DATASET_NAME + "/1",
					Arrays.asList(
							new TypedNamedEntity(23, 56, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(98, 24, "", new HashSet<String>(Arrays.asList(CELL_TYPE))),
							new TypedNamedEntity(142, 15, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(160, 3, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(184, 7, "", new HashSet<String>(Arrays.asList(CELL_TYPE))))),
			new DocumentImpl("These results imply that membrane potential changes secondary to the ligand-dependent opening of Ca(2+)-activated K+ channels are not involved in B and T lymphocyte activation and mitogenesis . ",
					"http://" + DATASET_NAME + "/2",
					Arrays.asList(
							new TypedNamedEntity(97, 28, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(146, 18, "", new HashSet<String>(Arrays.asList(CELL_TYPE))))),
			new DocumentImpl("Interestingly , GATA-2 and GATA-3 proteins also localized to the same nuclear bodies in cell lines co-expressing GATA -1 and -2 or GATA -1 and -3 gene products . ",
					"http://" + DATASET_NAME + "/3",
					Arrays.asList(
							new TypedNamedEntity(16, 6, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(27, 6, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(27, 15, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(113, 14, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(131, 14, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(113, 46, "", new HashSet<String>(Arrays.asList(PROTEIN))))),
			new DocumentImpl("An additional significant finding was than TNF mRNA induced in primed cells was much more stable than in unprimed cells ( T1/2 increased 6-8-fold ) . ",
					"http://" + DATASET_NAME + "/4",
					Arrays.asList(
							new TypedNamedEntity(43, 3, "", new HashSet<String>(Arrays.asList(PROTEIN))),
							new TypedNamedEntity(43, 8, "", new HashSet<String>(Arrays.asList(RNA))),
							new TypedNamedEntity(63, 12, "", new HashSet<String>(Arrays.asList(CELL_LINE))),
							new TypedNamedEntity(105, 14, "", new HashSet<String>(Arrays.asList(CELL_LINE))))));

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
