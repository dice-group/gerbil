package org.aksw.gerbil.utils;

import org.junit.Assert;
import org.junit.Test;

public class IDCreatorTest {

    @Test
    public void test() {
        String id;
        for (int i = 0; i < 100; ++i) {
            id = IDCreator.getInstance().createID();
            if ((i == 0) || (i == 99)) {
                System.out.println("Generated Id (#" + i + ") = \"" + id + "\"");
            }
            Assert.assertTrue(id + " doesn't end with " + Integer.toString(i), id.endsWith(Integer.toString(i)));
        }
    }
}
