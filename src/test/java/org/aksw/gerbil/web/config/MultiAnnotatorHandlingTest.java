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
package org.aksw.gerbil.web.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.web.config.AdapterManager;
import org.aksw.gerbil.web.config.AnnotatorsConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This test case makes sure that if multiple annotator configurations with the
 * same name are present, the most precise one will be chosen.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@RunWith(Parameterized.class)
public class MultiAnnotatorHandlingTest {

    private static final String ANNOTATOR_PROPERTIES_FILE = "multi_annotator_test.properties";
    private static final String ANNOTATOR_NAME = "TestAnnotator";

    private static AdapterManager adapterManager;

    @BeforeClass
    public static void initProperties() {
        // Load the annotator definitions we would like to use for testing
        GerbilConfiguration.loadAdditionalProperties(ANNOTATOR_PROPERTIES_FILE);
        adapterManager = new AdapterManager();
        adapterManager.setAnnotators(AnnotatorsConfig.annotators());
    }

    @SuppressWarnings("deprecation")
    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        testConfigs.add(new Object[] { ExperimentType.A2KB, "http://testannotator.org/test&mode=A2KB" });
        testConfigs.add(new Object[] { ExperimentType.C2KB, "http://testannotator.org/test&mode=A2KB" });
        testConfigs.add(new Object[] { ExperimentType.D2KB, "http://testannotator.org/test&mode=D2KB" });
        testConfigs.add(new Object[] { ExperimentType.Sa2KB, "http://testannotator.org/test&mode=A2KB" });
        testConfigs.add(new Object[] { ExperimentType.Sc2KB, "http://testannotator.org/test&mode=A2KB" });
        testConfigs.add(new Object[] { ExperimentType.Rc2KB, "http://testannotator.org/test&mode=A2KB" });
        testConfigs.add(new Object[] { ExperimentType.ERec, "http://testannotator.org/test&mode=ERec" });
        testConfigs.add(new Object[] { ExperimentType.ETyping, "http://testannotator.org/test&mode=ETyping" });
        testConfigs.add(new Object[] { ExperimentType.OKE_Task1, "http://testannotator.org/test&mode=OKE_Task1" });
        testConfigs.add(new Object[] { ExperimentType.OKE_Task2, null });
        return testConfigs;
    }

    private ExperimentType type;
    private String expectedUrl;

    public MultiAnnotatorHandlingTest(ExperimentType type, String expectedUrl) {
        this.type = type;
        this.expectedUrl = expectedUrl;
    }

    @Test
    public void test2() throws GerbilException {
        AnnotatorConfiguration annotatorConfig = adapterManager.getAnnotatorConfig(ANNOTATOR_NAME, type);
        if (expectedUrl == null) {
            Assert.assertNull(annotatorConfig);
        } else {
            Annotator annotator = annotatorConfig.getAnnotator(type);
            Assert.assertNotNull(annotator);
            Assert.assertNotNull("Got null as annotator but expected an annotator with URL=" + expectedUrl, annotator);
            Assert.assertEquals(expectedUrl, ((NIFBasedAnnotatorWebservice) annotator).getUrl());
        }
    }
}
