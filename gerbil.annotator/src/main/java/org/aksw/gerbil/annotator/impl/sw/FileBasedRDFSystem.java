package org.aksw.gerbil.annotator.impl.sw;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;

public class FileBasedRDFSystem extends InstanceListBasedAnnotator implements
		Annotator {

	public FileBasedRDFSystem(String annotatorName, List<Model> instances) {
		super(annotatorName, instances);
		// TODO Auto-generated constructor stub
	}

	protected static List<Model> loadInstances(List<String> qaldFiles,
			List<String> questionUriPrefixes) throws GerbilException {
		List<Model> instances = new ArrayList<Model>();
		for (int i = 0; i < qaldFiles.size(); ++i) {
			loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i),
					instances);
		}
		return instances;
	}

	protected static void loadInstances(String rdfFile,
			String questionUriPrefix, List<Model> instances)
			throws GerbilException {

			//TODO load rdf into instances
	}


	/**
	 * Constructor.
	 * 
	 * @param qaldFiles
	 *            the QALD files that contain the responses of this annotator
	 * @param questionUriPrefixes
	 *            the URI prefixes that are used for the single QALD files
	 * @throws GerbilException
	 *             if one of the given files can not be loaded correctly
	 */
	public FileBasedRDFSystem(List<String> qaldFiles,
			List<String> questionUriPrefixes) throws GerbilException {
		this(null, qaldFiles, questionUriPrefixes);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the annotator
	 * @param qaldFiles
	 *            the QALD files that contain the responses of this annotator
	 * @param questionUriPrefixes
	 *            the URI prefixes that are used for the single QALD files
	 * @throws GerbilException
	 *             if one of the given files can not be loaded correctly
	 */
	public FileBasedRDFSystem(String name, List<String> qaldFiles,
			List<String> questionUriPrefixes) throws GerbilException {
		super(name, loadInstances(qaldFiles, questionUriPrefixes));
	}


}
