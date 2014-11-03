package org.aksw.gerbil;

import java.util.Map;

import org.aksw.gerbil.datasets.DatahubNIFConfig;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;
import org.junit.Ignore;

@Ignore
public class DataHubIOTest {

    public static void main(String[] args) {
        DatahubNIFLoader loader = new DatahubNIFLoader();
        Map<String, String> datasets = loader.getDataSets();
        System.out.println(datasets.toString());
    }
}
