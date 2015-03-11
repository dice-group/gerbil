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
//      "gerbil_data/datasets/spotlight/dbpedia-spotlight-nif.ttl"};
            "gerbil_data/datasets/KORE50/kore50-nif.ttl",
            "C:\\Daten\\oke-challenge\\GoldStandard_sampleData\\task2\\dataset_task_2.ttl",
            "C:\\Daten\\oke-challenge\\GoldStandard_sampleData\\task1\\dataset_task_1.ttl",
            "C:\\Daten\\oke-challenge\\example_data\\task1.ttl", "C:\\Daten\\oke-challenge\\example_data\\task2.ttl",
            "C:\\Daten\\oke-challenge\\example_data\\task3.ttl" };

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
