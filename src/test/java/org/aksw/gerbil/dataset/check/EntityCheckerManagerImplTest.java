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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.check.impl.EntityCheckerManagerImpl;
import org.aksw.gerbil.dataset.check.impl.HttpBasedEntityChecker;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EntityCheckerManagerImplTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { new String[] { "http://dbpedia.org/resource/Berlin" },
                new String[] { "http://dbpedia.org/resource/Berlin" } });
        testConfigs.add(new Object[] {
                new String[] { "http://example.org/resource/Berlin", "http://dbpedia.org/resource/Berlin" },
                new String[] { "http://example.org/resource/Berlin", "http://dbpedia.org/resource/Berlin" } });
        testConfigs
                .add(new Object[] {
                        new String[] { "http://example.org/resource/Joe_DeAngelo",
                                "http://dbpedia.org/resource/Joe_DeAngelo" },
                        new String[] { "http://example.org/resource/Joe_DeAngelo",
                                "http://aksw.org/unknown_entity/http___dbpedia_org_resource_Joe_DeAngelo" } });
        return testConfigs;
    }

    private String uris[];
    private String expectedUris[];

    public EntityCheckerManagerImplTest(String uris[], String expectedUris[]) {
        this.uris = uris;
        this.expectedUris = expectedUris;
    }

    @Test
    public void test() {
        EntityCheckerManager manager = new EntityCheckerManagerImpl();
        manager.registerEntityChecker("http://dbpedia.org/resource/", new HttpBasedEntityChecker());

        Annotation annotation = new Annotation(new HashSet<String>(Arrays.asList(uris)));
        manager.checkMeanings(Arrays.asList(annotation));
        String annotationUris[] = annotation.getUris().toArray(new String[annotation.getUris().size()]);
        Arrays.sort(expectedUris);
        Arrays.sort(annotationUris);
        Assert.assertArrayEquals(expectedUris, annotationUris);
    }
}
