package org.aksw.gerbil.annotator.impl.sw;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class FileBasedRDFSystem extends InstanceListBasedAnnotator implements Annotator {

	
	
	public FileBasedRDFSystem(String annotatorName, List<Model> instances) {
		super(annotatorName, instances);
	}

	protected static List<Model> loadInstances(List<String> qaldFiles, List<String> questionUriPrefixes)
			throws GerbilException {
		List<Model> instances = new ArrayList<Model>();
		for (int i = 0; i < qaldFiles.size(); ++i) {
			loadInstances(qaldFiles.get(i), questionUriPrefixes.get(i), instances);
		}
		return instances;
	}

	protected static void loadInstances(String rdfFile, String questionUriPrefix, List<Model> instances)
			throws GerbilException {
		Model model = ModelFactory.createDefaultModel();
		// dataset = RDFDataMgr.loadModel(rdfpath);
		
		try(InputStream inputStream = new FileInputStream(rdfFile)){
		
			RDFDataMgr.read(model, rdfFile);
//			model = model.read(inputStream, null);
			// RDFDataMgr.read(nifModel, inputStream, getDataLanguage());
			instances.add(model);
		} catch (Exception e) {
			throw new GerbilException("Exception while parsing dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
		}

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
	public FileBasedRDFSystem(List<String> qaldFiles, List<String> questionUriPrefixes) throws GerbilException {
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
	public FileBasedRDFSystem(String name, List<String> qaldFiles, List<String> questionUriPrefixes)
			throws GerbilException {
		super(name, loadInstances(qaldFiles, questionUriPrefixes));
	}

    protected static void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
        }
    }
}
