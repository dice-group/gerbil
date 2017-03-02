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
package org.aksw.gerbil.dataset.impl.gerdaq;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class GERDAQDatasetTest {

    private static final String DATASET_NAME = "test";

    @Test
    public void checkLoadDatasets() throws Exception {
        File file = File.createTempFile("GERDAQ", ".xml");
        FileUtils.write(file,
                "<?xml version='1.0' encoding='UTF-8'?>" + String.format("%n")
                        + "<dataset><instance>loris <annotation rank_0_id=\"44017\" rank_0_score=\"0.925555555556\" rank_0_title=\"Candle\">candle</annotation> sampler</instance><instance><annotation rank_0_id=\"230699\" rank_0_score=\"0.666666666667\" rank_0_title=\"Conveyancing\">buying land</annotation> and <annotation rank_0_id=\"21883824\" rank_0_score=\"1.0\" rank_0_title=\"Arizona\">arizona</annotation></instance><instance>hip gry pl</instance></dataset>",
                Charsets.UTF_8);
        String docUriStart = GERDAQDataset.generateDocumentUri(DATASET_NAME, file.getName());

        List<Document> expectedDocuments = Arrays.asList(
                new DocumentImpl("loris candle sampler", docUriStart + 0,
                        Arrays.asList(new NamedEntity(6, 6, "http://dbpedia.org/resource/Candle"))),
                new DocumentImpl("buying land and arizona", docUriStart + 1,
                        Arrays.asList(new NamedEntity(0, 11, "http://dbpedia.org/resource/Conveyancing"),
                                new NamedEntity(16, 7, "http://dbpedia.org/resource/Arizona"))),
                new DocumentImpl("hip gry pl", docUriStart + 2, new ArrayList<Marking>(0)));

        GERDAQDataset dataset = new GERDAQDataset(file.getAbsolutePath());
        try {
            dataset.setName(DATASET_NAME);
            dataset.init();

            Assert.assertArrayEquals(expectedDocuments.toArray(new Document[3]),
                    dataset.getInstances().toArray(new Document[3]));
        } finally {
            dataset.close();
        }
    }

}
