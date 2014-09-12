package org.aksw.gerbil.bat.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;

public class DBpediaToWikiId {
	private static String fileName = "dbpediaids.ttl";

	private static Model model;

	private static PrefixMapping prefixes;

	static {
		File f = new File(fileName);
		if (f.exists()) {

			model = RDFDataMgr.loadModel(fileName);
		} else {
			model = ModelFactory.createDefaultModel();
		}
		prefixes = new PrefixMappingImpl()
				.withDefaultMappings(PrefixMapping.Extended);
		prefixes.setNsPrefix("nif",
				"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#");
		prefixes.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
		prefixes.setNsPrefix("itsrdf", "http://www.w3.org/2005/11/its/rdf#");
	}

	public static int getId(String dbpediaUri) {
		ParameterizedSparqlString query = new ParameterizedSparqlString(
				"SELECT ?id WHERE { ?dbpedia dbo:wikiPageID ?id .}", prefixes);
		query.setIri("dbpedia", dbpediaUri);
		QueryExecution qexec = QueryExecutionFactory.create(query.asQuery(),
				model);
		ResultSet result = qexec.execSelect();
		int id = -1;
		if (result.hasNext()) {
			id = result.next().get("id").asLiteral().getInt();
			return id;
		}
		qexec = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query.asQuery());
		result = qexec.execSelect();
		if (result.hasNext()) {
			id = result.next().get("id").asLiteral().getInt();
			model.add(new StatementImpl(model.createResource(dbpediaUri), model
					.createProperty("http://dbpedia.org/ontology/wikiPageID"),
					model.createTypedLiteral(id)));
			return id;
		}

		model.add(new StatementImpl(model.createResource(dbpediaUri), model
				.createProperty("http://dbpedia.org/ontology/wikiPageID"),
				model.createTypedLiteral(id)));
		return id;
	}

	public static void write() {

		try {
			model.write(new FileOutputStream(fileName), "TTL");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
