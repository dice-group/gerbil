package org.aksw.gerbil.utils;

import org.junit.Assert;
import org.junit.Test;

public class IDCreatorTest {

    @Test
    public void test() {
        String id;
        IDCreator.getInstance().setLastCreatedID("201410100000");
        for (int i = 0; i < 100; ++i) {
            id = IDCreator.getInstance().createID();
            if ((i == 0) || (i == 99)) {
                System.out.println("Generated Id (#" + i + ") = \"" + id + "\"");
            }
            Assert.assertTrue(id + " doesn't end with " + Integer.toString(i), id.endsWith(Integer.toString(i)));
        }
    }

    @Test
    public void testIdSet() {
        IDCreator.getInstance().setLastCreatedID("201410100009");
        String id = IDCreator.getInstance().createID();
        // make sure that the count (the last 4 digits) starts at 0
        Assert.assertEquals("0000", id.substring(id.length() - 4));

        String id2 = IDCreator.getInstance().createID();
        // make sure that the id is the same except the count
        Assert.assertEquals("0001", id2.substring(id.length() - 4));
        Assert.assertEquals(id.substring(0, id.length() - 4), id2.substring(0, id.length() - 4));
        Assert.assertFalse(id.equals(id2));
    }
}
