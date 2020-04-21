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
package org.aksw.gerbil.semantic.sameas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.impl.http.HTTPBasedSameAsRetriever;
import org.aksw.gerbil.test.HTTPServerMock;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HTTPBasedSameAsRetrieverTest extends HTTPServerMock {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { null, null });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/notInWiki/Peter_Pan", null });
        testConfigs.add(new Object[] { HTTP_SERVER_ADDRESS + "/resource/Kaufland",
                Arrays.asList("http://fr.dbpedia.org/resource/Kaufland", "http://de.dbpedia.org/resource/Kaufland") });
        testConfigs.add(new Object[] {HTTP_SERVER_ADDRESS + "/resource/People's_Republic_of_China",
                Arrays.asList("http://dbpedia.org/resource/China") });
        testConfigs.add(new Object[] {HTTP_SERVER_ADDRESS + "/resource/Malaysia",
        Arrays.asList("http://de.dbpedia.org/resource/Malaysia") });
        testConfigs.add(new Object[] {HTTP_SERVER_ADDRESS + "/resource/Home_Depot",
                Arrays.asList("http://dbpedia.org/resource/The_Home_Depot") });
 
        return testConfigs;
    }

    private String uri;
    private Set<String> expectedURIs;

    public HTTPBasedSameAsRetrieverTest(String uri, Collection<String> expectedURIs) {
        super(new HTTPBasedSameAsRetrieverContainer(uri, expectedURIs));
        this.uri = uri;
        if (expectedURIs != null) {
            this.expectedURIs = new HashSet<String>();
            this.expectedURIs.addAll(expectedURIs);
        }
    } 

    @Test
    public void test() {
        HTTPBasedSameAsRetriever retriever = new HTTPBasedSameAsRetriever();
        Set<String> uris = retriever.retrieveSameURIs(uri);
        if (expectedURIs == null) {
            Assert.assertNull(uris);
        } else {
            Assert.assertNotNull(uris);
            Assert.assertTrue(uris.toString() + " does not contain all of " + uris.toString(),
                    uris.containsAll(expectedURIs));
        }
        IOUtils.closeQuietly(retriever);

        Throwable serverError = ((HTTPBasedSameAsRetrieverContainer) container).getThrowable();
        //check if the server threw anything
        if(serverError != null) {
            throw new AssertionError("The server encountered an error:" + serverError);
        }
    }
}
