package org.aksw.gerbil.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class SingletonWikipediaApiTest {

    @Test
    public void test() throws IOException {
        Assert.assertNotNull(SingletonWikipediaApi.getInstance());
        Assert.assertEquals(28329803, SingletonWikipediaApi.getInstance().getIdByTitle("Spider"));
        Assert.assertEquals("Spider", SingletonWikipediaApi.getInstance().getTitlebyId(28329803));
    }
}
