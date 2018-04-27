package org.aksw.gerbil.annotator;

import org.aksw.gerbil.annotator.impl.sw.RDFWebServiceSystem;
import org.aksw.gerbil.datatypes.AbstractAdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public class RDFWebServiceConfiguration extends AbstractAdapterConfiguration implements AnnotatorConfiguration {

	private String uri;
	private File2SystemEntry fileMapping;

	public RDFWebServiceConfiguration(String name, boolean couldBeCached, ExperimentType applicableForExperiment, String uri) {
		super(name, couldBeCached, applicableForExperiment);
		this.uri=uri;
	}

	@Override
	public Annotator getAnnotator(ExperimentType type) throws GerbilException {
		if (applicableForExperiment.equalsOrContainsType(type)) {
			Annotator system =  new RDFWebServiceSystem(name, uri);
			system.setFileMapping(fileMapping);
			return system;
		}
		return null;
	}

	@Override
	public void setFileMapping(File2SystemEntry entry) {
		this.fileMapping=entry;
	}
}
