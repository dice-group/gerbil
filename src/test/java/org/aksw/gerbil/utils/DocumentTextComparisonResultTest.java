package org.aksw.gerbil.utils;

import java.util.Random;

import org.aksw.gerbil.utils.DocumentTextComparison.DocumentTextComparisonResult;
import org.aksw.gerbil.utils.DocumentTextComparison.DocumentTextEdits;
import org.junit.Assert;
import org.junit.Test;

public class DocumentTextComparisonResultTest {

    @Test
    public void testCreateMethod() {
        DocumentTextComparisonResult result;

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.NONE);
        Assert.assertEquals(DocumentTextEdits.NONE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.INSERT);
        Assert.assertEquals(DocumentTextEdits.INSERT, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.DELETE);
        Assert.assertEquals(DocumentTextEdits.DELETE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.SUBSTITUTE);
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.NONE, 1);
        Assert.assertEquals(DocumentTextEdits.NONE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.NONE, 2);
        Assert.assertEquals(DocumentTextEdits.NONE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.INSERT, 1);
        Assert.assertEquals(DocumentTextEdits.INSERT, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.INSERT, 2);
        Assert.assertEquals(DocumentTextEdits.INSERT, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.DELETE, 1);
        Assert.assertEquals(DocumentTextEdits.DELETE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.DELETE, 2);
        Assert.assertEquals(DocumentTextEdits.DELETE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.SUBSTITUTE, 1);
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.SUBSTITUTE, 2);
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, result.getStep(0));
        try {
            result.getStep(1);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }
    }

    @Test
    public void testCopyMethod() {
        DocumentTextComparisonResult result, copy;

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.NONE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.NONE);
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.NONE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.SUBSTITUTE);
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.INSERT);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.NONE);
        Assert.assertEquals(DocumentTextEdits.INSERT, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.INSERT);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.SUBSTITUTE);
        Assert.assertEquals(DocumentTextEdits.INSERT, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.DELETE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.NONE);
        Assert.assertEquals(DocumentTextEdits.DELETE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.DELETE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.SUBSTITUTE);
        Assert.assertEquals(DocumentTextEdits.DELETE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.SUBSTITUTE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.NONE);
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.NONE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }

        result = DocumentTextComparisonResult.create(1, DocumentTextEdits.SUBSTITUTE);
        copy = DocumentTextComparisonResult.create(result, DocumentTextEdits.SUBSTITUTE);
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(0));
        Assert.assertEquals(DocumentTextEdits.SUBSTITUTE, copy.getStep(1));
        try {
            copy.getStep(2);
            Assert.fail("An exception was expected in the line above");
        } catch (IllegalArgumentException e) {
            // That was expected
        }
    }

    @Test
    public void testAddAndGet() {
        final int STEPS = 100;
        DocumentTextComparisonResult result;
        DocumentTextEdits correctSteps[] = new DocumentTextEdits[STEPS];
        Random random = new Random();
        for (int i = 0; i < STEPS; ++i) {
            if(i == 31) {
                correctSteps[i] = DocumentTextEdits.INSERT;
            } else {
            correctSteps[i] = DocumentTextEdits.values()[random.nextInt(DocumentTextEdits.values().length)];
            }
        }

        // 1st test with correct size at init
        result = new DocumentTextComparisonResult(STEPS);
        for (int i = 0; i < STEPS; ++i) {
            result.addStep(correctSteps[i]);
        }
        for (int i = 0; i < STEPS; ++i) {
            Assert.assertEquals("Arrays first differed at element [" + i + "];", correctSteps[i], result.getStep(i));
        }
        
        // 2nd test without init
        result = new DocumentTextComparisonResult();
        for (int i = 0; i < STEPS; ++i) {
            result.addStep(correctSteps[i]);
        }
        for (int i = 0; i < STEPS; ++i) {
            Assert.assertEquals("Arrays first differed at element [" + i + "];", correctSteps[i], result.getStep(i));
        }
    }
}
