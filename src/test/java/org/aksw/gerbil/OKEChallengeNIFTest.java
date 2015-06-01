/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil;

import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
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
        }
    }
}
