package org.aksw.gerbil.dataid;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
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
import com.hp.hpl.jena.vocabulary.RDFS;

public class DataIDGenerator {

	public static String createDataIDModel(List<ExperimentTaskResult> list,
			String eID) {

		// test with dataset 201411060000 
		
		// definitions of ns
		String gerbilURI = "http://gerbil.aksw.org/ns#";
		String rdfURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		String rdfsURI = "http://www.w3.org/2000/01/rdf-schema#";
		String xsdURI = "http://www.w3.org/2001/XMLSchema#";
		String qbURI = "http://purl.org/linked-data/cube#";		
		
		String localURI = "http://139.18.2.164:1234/gerbil/";

		String gerbilType = "experimentType";
		String gerbilMatching = "matching";
		String gerbilDSD = "dsd";
		String gerbilD2W = "D2W";
		String gerbilA2W = "A2W";
		String gerbilSa2W = "Sa2W";
		String gerbilSc2W = "Sc2W";
		String gerbilRc2W = "Rc2W";
		

		// definiton of the gerbil experiments properties
		String gerbilExpetimentDatasetUsed = "evalDataset";
		String gerbilExperimentAnnotator = "annotator";
		String gerbilExperimentMicroF1 = "microF1";
		String gerbilExperimentMacroF1 = "macroF1";
		String gerbilExperimentMicroPrecision = "microPrecision";
		String gerbilExperimentMacroPrecision = "macroPrecision";
		String gerbilExperimentMicroRecall = "microRecall";
		String gerbilExperimentMacroRecall = "macroRecall";
		String gerbilExperimentTimestamp = "timestamp";
		String gerbilExperimentErrorCount = "errorCount";
		String gerbilExperimentStrongAnnoMatch = "StrongAnnoMatch";
		String gerbilExperimentWeakAnnotationMatch= "WeakAnnoMatch";
		

		
		// definition of the qb props
		String qbDataset = "Dataset";
		String qbStructure = "structure";
		String qbObservation = "observation";
		

		// create an empty JENA Model
		Model model = ModelFactory.createDefaultModel();

		// setting namespaces
		model.setNsPrefix("gerbil", gerbilURI);
		model.setNsPrefix("rdf", rdfURI);
		model.setNsPrefix("rdfs", rdfsURI);
		model.setNsPrefix("xsd", xsdURI);
		model.setNsPrefix("qb", qbURI);
		
		
		// model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

		// creating JENA properties for gerbil experiments service
		Property type = model.createProperty(gerbilURI + gerbilType);
		Property matching = model.createProperty(gerbilURI + gerbilMatching);
		Property dsdP = model.createProperty(gerbilURI
				+ gerbilDSD);

		// creating JENA properties for experiments
		Property dataset = model.createProperty(gerbilURI
				+ gerbilExpetimentDatasetUsed);
		Property qbDatasetP = model.createProperty(qbURI
				+ qbDataset);
		Property qbStructureP = model.createProperty(qbURI
				+ qbStructure);
		Property qbObservationP = model.createProperty(qbURI
				+ qbObservation);
		
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
		
		Property gerbilD2WP = model.createProperty(gerbilURI
				+ gerbilD2W);
		Property gerbilA2WP = model.createProperty(gerbilURI
				+ gerbilA2W);
		Property gerbilSa2WP = model.createProperty(gerbilURI
				+ gerbilSa2W);
		Property gerbilSc2WP = model.createProperty(gerbilURI
				+ gerbilSc2W);
		Property gerbilRc2WP = model.createProperty(gerbilURI
				+ gerbilRc2W);						

		// creating gerbil service resource
		Resource gerbilExp1 = model.createResource(localURI+"exp_1");

		gerbilExp1.addProperty(RDF.type, qbDatasetP);

		// setting literals for the service resource 
		Iterator<ExperimentTaskResult> e = list.iterator();
		ExperimentTaskResult first = e.next();
		
		String typeString = first.getType()
				.toString();
		String datasetString = first.getDataset();

		Property matchingP; 
		if(first.getMatching().toString().equals("WEAK_ANNOTATION_MATCH")){
			matchingP  = model.createProperty(gerbilURI
					+ gerbilExperimentWeakAnnotationMatch);						
		}
		else{
			matchingP  = model.createProperty(gerbilURI
					+ gerbilExperimentStrongAnnoMatch);	
		}
		
		model.add(gerbilExp1, RDFS.label, "Example dataset");
		model.add(gerbilExp1, qbStructureP, dsdP);

		int experimentNumber = 0;
		e = list.iterator();
		
		//iterating over the experiments
		while (e.hasNext()) {

			// increase expetiment number
			experimentNumber++;

			// get next experiment from the list
			ExperimentTaskResult i = e.next(); 

			// create experiment
			Resource exp_ann = model.createResource(localURI
					+ "ann_" + experimentNumber);

			exp_ann.addProperty(RDF.type, qbObservationP);
			exp_ann.addProperty(qbDatasetP,gerbilExp1); 
			
			// TODO make here a method,
			switch (typeString) {
			case "D2W":
				exp_ann.addProperty(type,gerbilD2WP);
				break;
			case "A2W":
				exp_ann.addProperty(type,gerbilA2WP);
				break;
			case "Sa2W":
				exp_ann.addProperty(type,gerbilSa2WP);
				break;				
			case "Sc2W":
				exp_ann.addProperty(type,gerbilSc2WP);
				break;
			case "Rc2W":
				exp_ann.addProperty(type,gerbilRc2WP);
				break;				
				
			default:
				break;
			}
			
			
			exp_ann.addProperty(annotator,gerbilURI+annotator+"/dataid.ttl");
			exp_ann.addProperty(dataset,gerbilURI+datasetString+"/dataid.ttl");
			
			exp_ann.addProperty(matching,matchingP);

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
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(i.getTimestamp()));
			
			Literal literalTimestamp = model.createTypedLiteral(cal);
			Literal literalErrorCount = model.createTypedLiteral(i
					.getErrorCount());

			model.add(exp_ann, experimentMicroF1, literalMicroF1Measure);
			model.add(exp_ann, experimentMacroF1, literalMacroF1Measure);
			model.add(exp_ann, experimentMicroPrecision,
					literalMicroPrecision);
			model.add(exp_ann, experimentMacroPrecision,
					literalMacroPrecision);
			model.add(exp_ann, experimentMicroRecall, literalMicroRecall);
			model.add(exp_ann, experimentMacroRecall, literalMacroRecall);
			model.add(exp_ann, experimentTimestamp, literalTimestamp);
			model.add(exp_ann, experimentErrorCount, literalErrorCount);

		}

		//writing dataid result to output (this should be removed)
//		RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);

		OutputStream o = new ByteArrayOutputStream();

		// creating json-ld output format
		RDFDataMgr.write(o, model, RDFFormat.JSONLD);

		return o.toString();
	}
}
