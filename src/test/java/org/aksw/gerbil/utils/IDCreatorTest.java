/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
