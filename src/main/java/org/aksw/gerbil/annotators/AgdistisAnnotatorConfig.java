package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.AgdistisAnnotator;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import org.aksw.gerbil.datatypes.ExperimentType;

public class AgdistisAnnotatorConfig extends AbstractAnnotatorConfiguration {

	public static final String ANNOTATOR_NAME = "AGDISTIS";

	private WikipediaApiInterface wikiApi;

	public AgdistisAnnotatorConfig(WikipediaApiInterface wikiApi) {
		super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
		this.wikiApi = wikiApi;
	}

	@Override
	protected TopicSystem loadAnnotator() throws Exception {
		return new AgdistisAnnotator(wikiApi);
	}
}
