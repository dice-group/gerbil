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
package org.aksw.gerbil.dataset.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.dataset.check.impl.HttpBasedEntityChecker;
import org.aksw.gerbil.test.HTTPServerMock;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HttpBasedEntityCheckerTest extends HTTPServerMock {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // DBpedia examples
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Berlin", true });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Joe_DeAngelo", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Carol_Tome", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Joe_DeAngelo", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Home_Depot_Supply", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Claudio_X._Gonzales", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Milledge_A._Hart_III", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Jerry_Shields", false });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/James_Senn", false });

        // Wikipedia examples
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/wiki/Berlin", true });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/wiki/Joe_DeAngelo", false });
        return testConfigs;
    }

    private String uri;
    private boolean expectedDecision;

    public HttpBasedEntityCheckerTest(String uri, boolean expectedDecision) {
        super(new HttpBasedEntityCheckerContainer(expectedDecision));
        this.uri = uri;
        this.expectedDecision = expectedDecision;
    }

    @Test
    public void test() {
        HttpBasedEntityChecker checker = new HttpBasedEntityChecker();
        Assert.assertEquals(expectedDecision, checker.entityExists(uri));
        IOUtils.closeQuietly(checker);

        Throwable serverError = ((HttpBasedEntityCheckerContainer) container).getThrowable();
        //check if the server threw anything
        if(serverError != null) {
            throw new AssertionError("The server encountered an error:" + serverError);
        }
    }
}
