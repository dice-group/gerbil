/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@Deprecated
public class SingletonWikipediaApiTest {

    @Test
    public void test() throws IOException {
        Assert.assertNotNull(SingletonWikipediaApi.getInstance());
        Assert.assertEquals(28329803, SingletonWikipediaApi.getInstance().getIdByTitle("Spider"));
        Assert.assertEquals("Spider", SingletonWikipediaApi.getInstance().getTitlebyId(28329803));
    }
}
