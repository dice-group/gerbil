package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.annotators.NERDAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.Microposts2014Config;
import org.aksw.gerbil.datasets.Microposts2014Config.Microposts2014Chunk;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;

/**
 * ...
 *
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class Microposts2014Test {

	public static void main(String[] args) {
		
		WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        ExperimentTaskConfiguration taskConfigs[] = 
        		new ExperimentTaskConfiguration[] 
        				{ 
        					new ExperimentTaskConfiguration(
        								new NERDAnnotatorConfig(wikiAPI), 
        								new Microposts2014Config(Microposts2014Chunk.TRAIN, SingletonWikipediaApi.getInstance()), 
        								ExperimentType.D2W,
        								Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, 
        											 new SimpleLoggingDAO4Debugging(), 
        											 taskConfigs,
        										     "NERD_TEST");
        experimenter.run();

	}

}
