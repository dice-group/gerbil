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


import it.unipi.di.acube.batframework.systemPlugins.DBPediaApi;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
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
public class SpotlightTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new IITBDatasetConfig(wikiAPI),
        // ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new AIDACoNLLDatasetConfig(
        // AIDACoNLLChunk.COMPLETE,
        // wikiAPI), ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new AQUAINTDatasetConfiguration(
        // wikiAPI), ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new ACE2004DatasetConfig(
        // wikiAPI), ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new MeijDatasetConfig(), ExperimentType.Rc2KB,
        // Matching.STRONG_ENTITY_MATCH) };
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
                ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_TEST");
        experimenter.run();
    }
}
