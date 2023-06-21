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
package org.aksw.gerbil.dataset.impl.conll;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractGenericCoNLLDatasetTest {

    private String fileContent;
    private String text;
    private Marking expectedMarking;
    private int documentId;
    private int markingId;

    public AbstractGenericCoNLLDatasetTest(String fileContent, String text, Marking expectedMarking, int documentId,
            int markingId) {
        this.fileContent = fileContent;
        this.text = text;
        this.expectedMarking = expectedMarking;
        this.documentId = documentId;
        this.markingId = markingId;
    }

    @Test
    public void test() throws IOException, GerbilException {
        // Create temporary file with given text
        File file = File.createTempFile("test-dataset-", ".tsv");
        FileUtils.write(file, fileContent);

        InitializableDataset dataset = createDataset(file);
        dataset.init();
        List<Document> documents = dataset.getInstances();
        Assert.assertNotNull(documents);
        Assert.assertTrue(documents.size() > documentId);
        Assert.assertEquals(text, documents.get(documentId).getText());
        List<Marking> markings = documents.get(documentId).getMarkings();
        Assert.assertNotNull(markings);
        Assert.assertTrue(markings.size() > markingId);
        Assert.assertEquals(expectedMarking, markings.get(markingId));
        dataset.close();
    }

    public abstract InitializableDataset createDataset(File file);

}
