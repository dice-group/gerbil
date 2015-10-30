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
package org.aksw.gerbil;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotator.impl.babelfy.BabelfyAnnotator;
import org.aksw.gerbil.annotators.AnnotatorConfigurationImpl;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
import org.aksw.gerbil.datasets.DatasetConfigurationImpl;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.aksw.gerbil.matching.Matching;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.junit.Ignore;

@Ignore
public class BabelfyTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException,
            NoSuchMethodException, SecurityException {
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
                new AnnotatorConfigurationImpl("", true, BabelfyAnnotator.class.getConstructor(), new Object[0],
                        ExperimentType.A2KB),
                new DatasetConfigurationImpl("OKE 2015 Task 1 example set", true,
                        FileBasedNIFDataset.class.getConstructor(String.class),
                        new Object[] { GerbilConfiguration.getInstance()
                                .getString("org.aksw.gerbil.datasets.OKE_2015_TASK1_EXAMPLE.file") },
                        ExperimentType.OKE_Task1),
                ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(new SimpleOverseer(), new SimpleLoggingDAO4Debugging(),
                new EvaluatorFactory(), taskConfigs, "Babelfy_TEST");
        experimenter.run();
    }
}
