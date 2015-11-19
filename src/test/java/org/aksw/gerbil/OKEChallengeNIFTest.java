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
package org.aksw.gerbil;

import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class OKEChallengeNIFTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OKEChallengeNIFTest.class);

    public static final String FILES[] = new String[] {
            // "gerbil_data/datasets/spotlight/dbpedia-spotlight-nif.ttl"};
            "gerbil_data/datasets/KORE50/kore50-nif.ttl",
            "C:/users/Micha/workspace/oke-challenge/GoldStandard_sampleData/task2/dataset_task_2.ttl",
            "C:/users/Micha/workspace/oke-challenge/GoldStandard_sampleData/task1/dataset_task_1.ttl",
            "C:/users/Micha/workspace/oke-challenge/example_data/task1.ttl",
            "C:/users/Micha/workspace/oke-challenge/example_data/task2.ttl" };

    public static void main(String[] args) {
        for (int i = 0; i < FILES.length; i++) {
            LOGGER.info("Testing \"{}\"", FILES[i]);
            FileBasedNIFDataset dataset = new FileBasedNIFDataset(FILES[i], "", Lang.TTL);
            try {
                dataset.init();
            } catch (Exception e) {
                LOGGER.error("Exception while reading dataset.", e);
            }
            IOUtils.closeQuietly(dataset);
        }
    }
}
