package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.IllinoisAnnotator_Server;

import org.aksw.gerbil.datatypes.ExperimentType;

public class IllinoisAnnotatorConfig extends AbstractAnnotatorConfiguration {

	public static final String ANNOTATOR_NAME = "";

	public IllinoisAnnotatorConfig() {
		super(ANNOTATOR_NAME, true, ExperimentType.Sa2W);
	}

	@Override
	protected TopicSystem loadAnnotator() throws Exception {
		return new IllinoisAnnotator_Server();
	}

}
