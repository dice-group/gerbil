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
package org.aksw.gerbil.dataset.impl.t4n;

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

public class Tweet4NeedDatasetTest {

	/*
	 * The test file cotains two special cases. One marking with the URI "*null*"
	 * and two markings that are overlapping.
	 */
	private static final String TEST_TEXT_DIR = "src/test/resources/datasets/twitterneed/";
	private static final String DATASET_NAME = "t4nTestDataset";

	private static final String EXPECTED_DOCUMENT_URI1 = "http://t4nTestDataset/100000000001";
	private static final String EXPECTED_TEXT1 = "John Oliver is the best anchor ever. #lwn";
	private static final Marking EXPECTED_MARKINGS1[] = new Marking[] { (Marking) new NamedEntity(0, 11,
			new HashSet<String>(Arrays.asList("http://dbpedia.org/resource/John_Oliver"))) };

	private static final String EXPECTED_DOCUMENT_URI2 = "http://t4nTestDataset/100000000003";
	private static final String EXPECTED_TEXT2 = "I believe in Cristiano Ronaldo #CR7Forever";
	private static final Marking EXPECTED_MARKINGS2[] = new Marking[] { (Marking) new NamedEntity(13, 17,
			new HashSet<String>(Arrays.asList("http://dbpedia.org/resource/Cristiano_Ronaldo"))) };

	@Test
	public void test() throws GerbilException {
		Tweet4NeedDataset dataset = new Tweet4NeedDataset(TEST_TEXT_DIR);
		dataset.setName(DATASET_NAME);
		dataset.init();
		boolean testPassed = true;
		for (Document document : dataset.getInstances()) {
			if (document.getDocumentURI().equals(EXPECTED_DOCUMENT_URI1)) {
				testPassed = performTest(document, EXPECTED_TEXT1, EXPECTED_MARKINGS1);
			} else if (document.getDocumentURI().equals(EXPECTED_DOCUMENT_URI2)) {
				testPassed = performTest(document, EXPECTED_TEXT2, EXPECTED_MARKINGS2);
			}
			Assert.assertTrue(testPassed);
		}
		IOUtils.closeQuietly(dataset);
	}

	private boolean performTest(Document document, String text, Marking[] markings) {
		Assert.assertEquals(text, document.getText());

		Set<Marking> expectedNEs = new HashSet<Marking>(Arrays.asList(markings));
		for (Marking marking : document.getMarkings()) {
			Assert.assertTrue("Couldn't find " + marking.toString() + " inside " + expectedNEs.toString(),
					expectedNEs.contains(marking));
		}
		Assert.assertEquals(expectedNEs.size(), document.getMarkings().size());
		return true;
	}

	public static void main(String[] args) throws GerbilException {
		Tweet4NeedDatasetTest test = new Tweet4NeedDatasetTest();
		test.test();
	}
}
