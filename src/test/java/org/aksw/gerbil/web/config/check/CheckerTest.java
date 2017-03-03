package org.aksw.gerbil.web.config.check;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CheckerTest {

    @Parameters
    public static Collection<Object[]> data() throws IOException {
        File existingFile = File.createTempFile("checker", "test");
        File deletedFile = File.createTempFile("checker", "test");
        Assert.assertTrue(deletedFile.delete());

        File existingDir = File.createTempFile("checker", "test");
        Assert.assertTrue(existingDir.delete());
        Assert.assertTrue(existingDir.mkdir());

        List<Object[]> testConfigs = new ArrayList<Object[]>();

        FileChecker fileChecker = new FileChecker();
        // Check existing file
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingFile }, true });
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingFile.toString() }, true });
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingFile.getAbsolutePath() }, true });
        // Check deleted file
        testConfigs.add(new Object[] { fileChecker, new Object[] { deletedFile }, false });
        testConfigs.add(new Object[] { fileChecker, new Object[] { deletedFile.toString() }, false });
        testConfigs.add(new Object[] { fileChecker, new Object[] { deletedFile.getAbsolutePath() }, false });
        // Check more than one file
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingFile, existingFile }, true });
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingFile, deletedFile }, false });
        testConfigs.add(new Object[] { fileChecker, new Object[] { deletedFile, deletedFile }, false });
        // Check existing directory
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingDir }, false });
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingDir.toString() }, false });
        testConfigs.add(new Object[] { fileChecker, new Object[] { existingDir.getAbsolutePath() }, false });

        DirectoryChecker dirChecker = new DirectoryChecker();
        // Check existing directory
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingDir }, true });
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingDir.toString() }, true });
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingDir.getAbsolutePath() }, true });
        // Check deleted file
        testConfigs.add(new Object[] { dirChecker, new Object[] { deletedFile }, false });
        testConfigs.add(new Object[] { dirChecker, new Object[] { deletedFile.toString() }, false });
        testConfigs.add(new Object[] { dirChecker, new Object[] { deletedFile.getAbsolutePath() }, false });
        // Check more than one directory
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingDir, existingDir }, true });
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingDir, deletedFile }, false });
        testConfigs.add(new Object[] { dirChecker, new Object[] { deletedFile, deletedFile }, false });
        // Check existing file
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingFile }, false });
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingFile.toString() }, false });
        testConfigs.add(new Object[] { dirChecker, new Object[] { existingFile.getAbsolutePath() }, false });

        return testConfigs;
    }

    private Checker checker;
    private Object arguments[];
    private boolean expectedResult;

    public CheckerTest(Checker checker, Object[] arguments, boolean expectedResult) {
        this.checker = checker;
        this.arguments = arguments;
        this.expectedResult = expectedResult;
    }

    @Test
    public void test() {
        Assert.assertEquals(expectedResult, checker.check(arguments));
    }
}
