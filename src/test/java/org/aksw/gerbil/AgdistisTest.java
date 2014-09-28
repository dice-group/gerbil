package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;

public class AgdistisTest {

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
		ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
				new AgdistisAnnotatorConfig(SingletonWikipediaApi.getInstance()),
				new KnownNIFFileDatasetConfig(NIFDatasets.KORE50),
				ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH) };
		Experimenter experimenter = new Experimenter(wikiAPI,
				new SimpleLoggingDAO4Debugging(), taskConfigs, "ILLINOIS_TEST");
		experimenter.run();
	}
}
