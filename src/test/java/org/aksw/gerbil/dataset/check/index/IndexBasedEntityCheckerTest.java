package org.aksw.gerbil.dataset.check.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A simple test that writes a temporary index using the {@link #CORRECT_URIS}
 * array and uses this to test the {@link IndexBasedEntityChecker} class.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@RunWith(Parameterized.class)
public class IndexBasedEntityCheckerTest {

    public static final String[] CORRECT_URIS = new String[] { "http://dbpedia.org/resource/Berlin",
            "http://dbpedia.org/resource/Michael_Müller_(politician)",
            "http://dbpedia.org/resource/Michael_M%C3%BCller_%28politician%29" };

    private static String indexDir;

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // DBpedia examples
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Berlin", true });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Paris", false });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Michael_Müller_(politician)", true });
        testConfigs.add(new Object[] { "http://dbpedia.org/resource/Michael_M%C3%BCller_%28politician%29", true });
        return testConfigs;
    }

    @BeforeClass
    public static void createIndex() {
        // Generate a temporary folder
        indexDir = FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis();
        (new File(indexDir)).mkdir();
        Indexer indexer = Indexer.create(indexDir);
        for (int i = 0; i < CORRECT_URIS.length; ++i) {
            indexer.index(CORRECT_URIS[i]);
        }
        indexer.close();
    }

    private String uri;
    private boolean expectedDecision;

    public IndexBasedEntityCheckerTest(String uri, boolean expectedDecision) {
        this.uri = uri;
        this.expectedDecision = expectedDecision;
    }

    @Test
    public void test() {
        IndexBasedEntityChecker checker = null;
        try {
            checker = IndexBasedEntityChecker.create(indexDir);
            Assert.assertEquals(expectedDecision, checker.entityExists(uri));
        } finally {
            IOUtils.closeQuietly(checker);
        }
    }
}
