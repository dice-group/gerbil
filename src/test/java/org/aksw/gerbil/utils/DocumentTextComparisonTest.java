package org.aksw.gerbil.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.utils.DocumentTextComparison.DocumentTextComparisonResult;
import org.aksw.gerbil.utils.DocumentTextComparison.DocumentTextEdits;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DocumentTextComparisonTest {

    public static final DocumentTextEdits D = DocumentTextEdits.DELETE;
    public static final DocumentTextEdits I = DocumentTextEdits.INSERT;
    public static final DocumentTextEdits N = DocumentTextEdits.NONE;
    public static final DocumentTextEdits S = DocumentTextEdits.SUBSTITUTE;

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { "Text", "Text", new DocumentTextEdits[] { N, N, N, N } });
        testConfigs.add(new Object[] { "Text", "", new DocumentTextEdits[] { I, I, I, I } });
        testConfigs.add(new Object[] { "", "Text", new DocumentTextEdits[] { D, D, D, D } });
        testConfigs.add(new Object[] { "", "", new DocumentTextEdits[] {} });

        // examples from
        // https://en.wikipedia.org/wiki/Wagner%E2%80%93Fischer_algorithm
        testConfigs.add(new Object[] { "kitten", "sitting", new DocumentTextEdits[] { S, N, N, N, S, N, D } });
        testConfigs.add(new Object[] { "Saturday", "Sunday", new DocumentTextEdits[] { N, I, I, N, S, N, N, N } });

        return testConfigs;
    }

    private String originalText;
    private String newText;
    private DocumentTextEdits expectedEdits[];

    public DocumentTextComparisonTest(String originalText, String newText, DocumentTextEdits[] expectedEdits) {
        this.originalText = originalText;
        this.newText = newText;
        this.expectedEdits = expectedEdits;
    }

    @Test
    public void testLevenstein() {
        DocumentTextComparisonResult result = DocumentTextComparison.getLevensteinDistance(newText, originalText);
        // DocumentTextEdits resultEdits[] = new
        // DocumentTextEdits[result.getNumberOfSteps()];
        for (int i = 0; i < expectedEdits.length; ++i) {
            Assert.assertEquals("Arrays first differed at element [" + i + "];", expectedEdits[i], result.getStep(i));
            // resultEdits[i] = result.getStep(i);
        }
        // Assert.assertArrayEquals(expectedEdits, resultEdits);
    }
}
