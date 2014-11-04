package org.aksw.gerbil.dataid;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class DataIDGenerator {

	public static String createDataIDModel(List<ExperimentTaskResult> list,
			String eID) {

		// definitions of the gerbil services properties
		String gerbilURI = "http://somewhere/gerbil";
		String localURI = "http://local/";

		String gerbilID = "id";
		String gerbilType = "type";
		String gerbilMatching = "match";
		String gerbilExperiment = "experiment";
		String gerbilExperimentService = "experimentService";

		// definiton of the gerbil experiments properties
		String gerbilExpetimentDatasetUsed = "dataset";
		String gerbilExperimentAnnotator = "annotator";
		String gerbilExperimentMicroF1 = "microf1";
		String gerbilExperimentMacroF1 = "macrof1";
		String gerbilExperimentMicroPrecision = "microPrecision";
		String gerbilExperimentMacroPrecision = "macroPrecision";
		String gerbilExperimentMicroRecall = "microRecall";
		String gerbilExperimentMacroRecall = "macroRecall";
		String gerbilExperimentTimestamp = "timestamp";
		String gerbilExperimentErrorCount = "errorCount";

		// create an empty JENA Model
		Model model = ModelFactory.createDefaultModel();

		// setting namespaces
		model.setNsPrefix("gerbil", gerbilURI);
		// model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

		// creating JENA properties for gerbil experiments service
		Property id = model.createProperty(gerbilURI + gerbilID);
		Property type = model.createProperty(gerbilURI + gerbilType);
		Property matching = model.createProperty(gerbilURI + gerbilMatching);
		Property experiment = model
				.createProperty(gerbilURI + gerbilExperiment);
		Property experimentService = model.createProperty(gerbilURI
				+ gerbilExperimentService);

		// creating JENA properties for experiments
		Property dataset = model.createProperty(gerbilURI
				+ gerbilExpetimentDatasetUsed);
		Property annotator = model.createProperty(gerbilURI
				+ gerbilExperimentAnnotator);
		Property experimentMicroF1 = model.createProperty(gerbilURI
				+ gerbilExperimentMicroF1);
		Property experimentMacroF1 = model.createProperty(gerbilURI
				+ gerbilExperimentMacroF1);
		Property experimentMicroPrecision = model.createProperty(gerbilURI
				+ gerbilExperimentMicroPrecision);
		Property experimentMacroPrecision = model.createProperty(gerbilURI
				+ gerbilExperimentMacroPrecision);
		Property experimentMicroRecall = model.createProperty(gerbilURI
				+ gerbilExperimentMicroRecall);
		Property experimentMacroRecall = model.createProperty(gerbilURI
				+ gerbilExperimentMacroRecall);
		Property experimentTimestamp = model.createProperty(gerbilURI
				+ gerbilExperimentTimestamp);
		Property experimentErrorCount = model.createProperty(gerbilURI
				+ gerbilExperimentErrorCount);

		// creating gerbil service resource
		Resource gerbilService = model.createResource(localURI);

		gerbilService.addProperty(RDF.type, experimentService);

		// setting literals for the service resource 
		Iterator<ExperimentTaskResult> e = list.iterator();
		ExperimentTaskResult first = e.next();

		Literal literalID = model.createTypedLiteral(eID);
		Literal literalType = model.createTypedLiteral(first.getType()
				.toString());
		Literal literalMatching = model.createTypedLiteral(first.getMatching()
				.toString());

		model.add(gerbilService, id, literalID);
		model.add(gerbilService, type, literalType);
		model.add(gerbilService, matching, literalMatching);

		int experimentNumber = 0;
		e = list.iterator();

		//iterating over the experiments
		while (e.hasNext()) {

			// increase expetiment number
			experimentNumber++;

			// get next experiment from the list
			ExperimentTaskResult i = e.next();

			// create experiment
			Resource experimentTmp = model.createResource(localURI
					+ "experiment" + experimentNumber);

			// add experiment to gerbil service resource
			model.add(gerbilService, experiment, experimentTmp);

			experimentTmp.addProperty(RDF.type, experiment);

			// creating and setting literals for the current experiment
			Literal literalMicroF1Measure = model.createTypedLiteral(i
					.getMicroF1Measure());
			Literal literalMacroF1Measure = model.createTypedLiteral(i
					.getMacroF1Measure());
			Literal literalMicroPrecision = model.createTypedLiteral(i
					.getMicroPrecision());
			Literal literalMacroPrecision = model.createTypedLiteral(i
					.getMacroPrecision());
			Literal literalMicroRecall = model.createTypedLiteral(i
					.getMicroRecall());
			Literal literalMacroRecall = model.createTypedLiteral(i
					.getMacroRecall());
			Literal literalTimestamp = model.createTypedLiteral(i
					.getTimestampstring());
			Literal literalErrorCount = model.createTypedLiteral(i
					.getErrorCount());

			model.add(experimentTmp, dataset, i.getDataset());
			model.add(experimentTmp, annotator, i.getAnnotator());
			model.add(experimentTmp, experimentMicroF1, literalMicroF1Measure);
			model.add(experimentTmp, experimentMacroF1, literalMacroF1Measure);
			model.add(experimentTmp, experimentMicroPrecision,
					literalMicroPrecision);
			model.add(experimentTmp, experimentMacroPrecision,
					literalMacroPrecision);
			model.add(experimentTmp, experimentMicroRecall, literalMicroRecall);
			model.add(experimentTmp, experimentMacroRecall, literalMacroRecall);
			model.add(experimentTmp, experimentTimestamp, literalTimestamp);
			model.add(experimentTmp, experimentErrorCount, literalErrorCount);

		}

		//writing dataid result to output (this should be removed)
		RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);

		OutputStream o = new ByteArrayOutputStream();

		// creating json-ld format
		RDFDataMgr.write(o, model, RDFFormat.JSONLD);

		return o.toString();
	}
}
