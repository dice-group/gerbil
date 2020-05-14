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
package org.aksw.gerbil.dataset.impl.erd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class ERDDataset2Test {
    
    private static final String TEST_QUERIES_FILE = "TREC-1\tadobe indian houses\n"
            + "TREC-7\tbowflex power pro\n"
            + "TREC-8\tbrooks brothers clearance\n"
            + "TREC-11\tcass county missouri\n"
            + "TREC-22\teast ridge high school\n"
            + "TREC-30\tgmat prep classes";

    private static final String TEST_ANNOTATIONS_FILE = "TREC-7\t0\thttp://dbpedia.org/resource/Bowflex\tbowflex\t1\n"
            + "TREC-8\t0\thttp://dbpedia.org/resource/Brooks_Brothers\tbrooks brothers\t1\n"
            + "TREC-11\t0\thttp://dbpedia.org/resource/Cass_County,_Missouri\tcass county missouri\t1\n"
            + "TREC-22\t0\thttp://dbpedia.org/resource/East_Ridge_High_School_(Kentucky)\teast ridge high school\t1\n"
            + "TREC-22\t1\thttp://dbpedia.org/resource/East_Ridge_High_School_(Florida)\teast ridge high school\t1\n"
            + "TREC-30\t0\thttp://dbpedia.org/resource/Test_preparation\tprep\t1\n";

    private static final List<Document> EXPECTED_DOCUMENTS = Arrays.asList(
        new DocumentImpl("adobe indian houses", "http://test/TREC-1", new ArrayList<Marking>(0)),
        new DocumentImpl("bowflex power pro", "http://test/TREC-7",
                Arrays.asList(new NamedEntity(0, 7, "http://dbpedia.org/resource/Bowflex"))),
        new DocumentImpl("brooks brothers clearance", "http://test/TREC-8",
                Arrays.asList(new NamedEntity(0, 15, "http://dbpedia.org/resource/Brooks_Brothers"))),
        new DocumentImpl("cass county missouri", "http://test/TREC-11",
                Arrays.asList(new NamedEntity(0, 20, "http://dbpedia.org/resource/Cass_County,_Missouri"))),
        new DocumentImpl("east ridge high school", "http://test/TREC-22",
                Arrays.asList(new NamedEntity(0, 22, "http://dbpedia.org/resource/East_Ridge_High_School_(Kentucky)"),
                    new NamedEntity(0, 22, "http://dbpedia.org/resource/East_Ridge_High_School_(Florida)"))),
        new DocumentImpl("gmat prep classes", "http://test/TREC-30",
                Arrays.asList(new NamedEntity(5, 4, "http://dbpedia.org/resource/Test_preparation"))));

    private static final String DATASET_NAME = "test";

    @Test
    public void checkLoadDatasets() throws Exception {
        File annotationFile = File.createTempFile("annotation", ".txt");
        File queryFile = File.createTempFile("query", ".txt");
        FileUtils.write(annotationFile, TEST_ANNOTATIONS_FILE, StandardCharsets.UTF_8.toString());
        FileUtils.write(queryFile, TEST_QUERIES_FILE, StandardCharsets.UTF_8.toString());

        ERDDataset2 dataset = new ERDDataset2(queryFile.getAbsolutePath(), annotationFile.getAbsolutePath());
        try {
            dataset.setName(DATASET_NAME);
            dataset.init();

            int size = EXPECTED_DOCUMENTS.size();
            Assert.assertArrayEquals(EXPECTED_DOCUMENTS.toArray(new Document[size]),
                    dataset.getInstances().toArray(new Document[size]));
        } finally {
            dataset.close();
        }
    }
}