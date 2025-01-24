package org.aksw.gerbil.io.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SimpleJsonIOTest {

    @Parameters
    public static Collection<Object[]> data() throws IOException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        TurtleNIFParser parser = new TurtleNIFParser();
        try (InputStream in = SimpleJsonIOTest.class.getClassLoader()
                .getResourceAsStream("annotator_examples/DBpedia_Spotlight-OKE_2015_Task_1_example_set-w-A2KB.ttl")) {
            Assert.assertNotNull(in);
            testConfigs.add(new Object[] { parser.parseNIF(in) });
        }
        try (InputStream in = SimpleJsonIOTest.class.getClassLoader()
                .getResourceAsStream("annotator_examples/FOX-OKE_2015_Task_1_example_set-w-A2KB.ttl")) {
            Assert.assertNotNull(in);
            testConfigs.add(new Object[] { parser.parseNIF(in) });
        }
        try (InputStream in = SimpleJsonIOTest.class.getClassLoader()
                .getResourceAsStream("annotator_examples/NERD_ML-OKE_2015_Task_1_example_set-w-A2KB.ttl")) {
            Assert.assertNotNull(in);
            testConfigs.add(new Object[] { parser.parseNIF(in) });
        }

        return testConfigs;
    }

    protected List<Document> documents;

    public SimpleJsonIOTest(List<Document> documents) {
        super();
        this.documents = documents;
    }

    @Test
    public void test() throws IOException {
        SimpleJsonDatasetWriter writer = new SimpleJsonDatasetWriter();
        String data = writer.writeDocuments(documents);
        SimpleJsonDatasetReader reader = new SimpleJsonDatasetReader();
        List<Document> readDocuments = reader.readDocuments(data);
        Assert.assertArrayEquals(documents.toArray(), readDocuments.toArray());
    }
}
