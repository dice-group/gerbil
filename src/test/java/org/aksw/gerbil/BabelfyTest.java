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
package org.aksw.gerbil;


import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.junit.Ignore;

import java.io.FileNotFoundException;
import java.io.IOException;

@Ignore
public class BabelfyTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        // ExperimentTaskConfiguration taskConfigs[] = new
        // ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()), new
        // KnownNIFFileDatasetConfig(
        // SingletonWikipediaApi.getInstance(),
        // NIFDatasets.N3_REUTERS_128), ExperimentType.D2KB,
        // Matching.STRONG_ANNOTATION_MATCH) };
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH),
                new ExperimentTaskConfiguration(new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(),
                                NIFDatasets.DBPEDIA_SPOTLIGHT), ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(),
                taskConfigs, "BABELFY_TEST");
        experimenter.run();
    }
}
