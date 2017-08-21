package org.aksw.gerbil.utils;

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
}
