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
import org.aksw.gerbil.annotators.NERDAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.Microposts2014Config;
import org.aksw.gerbil.datasets.Microposts2014Config.Microposts2014Chunk;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.junit.Ignore;

/**
 * Class for testing the microposts dataset.
 * 
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
@Ignore
public class Microposts2014Test {

    public static void main(String[] args) {

        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        ExperimentTaskConfiguration taskConfigs[] =
                new ExperimentTaskConfiguration[]
                {
                        new ExperimentTaskConfiguration(
                                new NERDAnnotatorConfig(wikiAPI),
                                new Microposts2014Config(Microposts2014Chunk.TRAIN, SingletonWikipediaApi.getInstance()),
                                ExperimentType.D2KB,
                                Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(),
                new SimpleLoggingDAO4Debugging(),
                taskConfigs,
                "MICROPOSTS_TEST");
        experimenter.run();

    }

}
