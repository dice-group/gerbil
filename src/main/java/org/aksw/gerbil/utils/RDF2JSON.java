package org.aksw.gerbil.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.Var;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Utility Class to convert qald rdf to qald json
 * 
 *  (TODO : provide qald rdf to eqald json) 
 * 
 * @author F. Conrads
 *
 */
public class RDF2JSON {

	private static final String QUESTION_TEXT = "http://qald-gen.aksw.org/vocab#text";
	private static final String QALD_RDF_PRE = "http://qald-gen.aksw.org/";
	private static final String SPARQL = "http://lsq.aksw.org/vocab#text";
	private static final String ANSWER = "http://lsq.aksw.org/vocab#answers";
	private static final String IS_BOOLEAN = "http://qald-gen.aksw.org/vocab#isBooleanAnswer";

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Usage: rdf2json.sh [eqald|qald] <QALD RDF Inadd file> <QALD Json outadd file>");
		} else {
			File json = new File(args[2]);
			json.createNewFile();
			OutputStream os = new FileOutputStream(json);
			InputStream is = new FileInputStream(new File(args[1]));
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(is, decoder);
	        if(args[0].equals("qald")) {
	        	qaldRdf2qaldJson(reader, os, "TTL");
	        }
	        else {
	        	qaldRdf2eqaldJson(reader, os, "TTL", args[1].replace("[^A-Za-z0-9]", ""));
	        }
		}
	}

	public static void qaldRdf2qaldJson(InputStreamReader rdf, OutputStream json, String lang) {
		Model qaldAsRdf = ModelFactory.createDefaultModel();
		qaldAsRdf = qaldAsRdf.read(rdf, QALD_RDF_PRE, lang);
		JsonObject qaldAsJson = qaldRdf2qaldJson(qaldAsRdf);

		try {		
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			
			json.write(gson.toJson(qaldAsJson).getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static JsonObject qaldRdf2qaldJson(Model rdf) {
		JsonObject qaldAsJson = new JsonObject();
		JsonArray questions = new JsonArray();
		qaldAsJson.add("questions", questions);
		Property text = rdf.createProperty(QUESTION_TEXT);
		Property sparqlProp = rdf.createProperty(SPARQL);
		Property answerProp = rdf.createProperty(ANSWER);
		Property booleanAnswer = rdf.createProperty(IS_BOOLEAN);
		StmtIterator questionIDs = rdf.listStatements(null, text, (RDFNode) null);
		int zeroAnswer =0;
		int zeroAnswerService=0;
		
		int zeroAnswerBoth=0;

		while (questionIDs.hasNext()) {
			JsonObject question = new JsonObject();
			Statement question2Text = questionIDs.next();

			// SET ID
			Resource qID = question2Text.getSubject();
			String id = qID.getURI().replace(QALD_RDF_PRE + "question#", "");
			question.add("id", new JsonPrimitive(id));

			// SET ACTUAL QUESTIONS
			JsonArray questionArr = new JsonArray();
			JsonObject questionArrObj = new JsonObject();
			questionArrObj.add("language", new JsonPrimitive("en"));
			questionArrObj.add("string", new JsonPrimitive(question2Text.getObject().asLiteral().toString()));
			questionArr.add(questionArrObj);
			question.add("question", questionArr);
			
			// SET SPARQL
			JsonObject sparql = new JsonObject();
			String sparqlString = rdf.listStatements(qID, sparqlProp, (RDFNode) null).next().getObject().toString();

			
			sparql.add("sparql", new JsonPrimitive(sparqlString.replace("\n", " ")));
			
			question.add("query", sparql);
			
			Boolean isZeroAnswer=false;
			
			// SET ANSWERS
			JsonArray answers = new JsonArray();

			JsonObject answer = new JsonObject();
			StmtIterator booltmp = rdf.listStatements(qID, booleanAnswer, (RDFNode) null);
			Boolean isBoolean = false;
			if(booltmp.hasNext()) {
				RDFNode obj = booltmp.next().getObject();
				isBoolean= obj.toString().equals("1");
			}
			else {
				Query q= null;
				try {
					q = QueryFactory.create(sparqlString);
				}catch(Exception e) {
					System.err.println("Question "+qID+": SPARQL cannot be read, ignoring question ");
					continue;
				}
				if(q.isAskType()) {
					isBoolean=true;
				}
			}
			 
			if (isBoolean) {
				answer.add("head", new JsonObject());
				Boolean boolAnswer = Boolean
						.valueOf(rdf.listStatements(qID, answerProp, (RDFNode) null).next().getObject().toString());
				answer.add("boolean", new JsonPrimitive(boolAnswer));
			} else {
				Query q= null;
				try {
					q = QueryFactory.create(sparqlString);
				}catch(Exception e) {
					System.err.println("Question "+qID+": SPARQL cannot be read, ignoring question ");
					continue;
				}
				JsonArray vars = new JsonArray();

				for (Var v : q.getProjectVars()) {
					vars.add(new JsonPrimitive(v.getName()));
				}
				JsonObject head = new JsonObject();
				head.add("vars", vars);
				answer.add("head", head);

				JsonObject results = new JsonObject();
				JsonArray bindings = new JsonArray();

				// 1. get answer text
				StmtIterator tmp = rdf.listStatements(qID, answerProp, (RDFNode) null);

				if(tmp.hasNext()) {
					String answerString = tmp.next().getObject().toString();
				// 2. split by ,
					String[] answerStrings = answerString.split("\\), ");
					if(answerString.isEmpty()) {
						isZeroAnswer =true;
						zeroAnswer ++;
					}
					parseAnswers(bindings, answerStrings);
				}
				else {
					isZeroAnswer =true;

					zeroAnswer++;
				}
				results.add("bindings", bindings);
				answer.add("results", results);
				int res = ResultSetFormatter.consume(QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", q).execSelect());
				if(res<=0) {
					if(isZeroAnswer) {
						zeroAnswerBoth++;
					}
					zeroAnswerService++;
				}
			}
			answers.add(answer);
			question.add("answers", answers);
			questions.add(question);

		}
		System.out.println("Questions with zero answer: "+zeroAnswer);
		System.out.println("Questions with zero answer using DBpedia: "+zeroAnswerService);
		System.out.println("Questions with zero answer in file as well as DBpedia: "+zeroAnswerBoth);
		return qaldAsJson;
	}


	@SuppressWarnings("unchecked")
	private static void parseAnswers(JsonArray bindings, String[] answerStrings) {
		for (String answer : answerStrings) {
			JsonObject binding = new JsonObject();
			//( ?uri = <http://dbpedia.org/resource/HC_Lada_Togliatti> ) ( ?x = <http://dbpedia.org/resource/Alexei_Emelin> )
			String[] varAnswers = answer.split("\\) \\(");
			//( ?uri = <http://dbpedia.org/resource/HC_Lada_Togliatti>  
			// ?x = <http://dbpedia.org/resource/Alexei_Emelin> )
			for(String var : varAnswers) {
				var = var.replace("(", "").replace(")", "").trim();
				//?uri = <http://dbpedia.org/resource/HC_Lada_Togliatti>
				if(var.isEmpty()) {
					continue;
				}
				String[] var2answer = var.split(" = ");
				//[?uri, <http://dbpedia.org/resource/HC_Lada_Togliatti>]
				JsonObject varAnsw = new JsonObject();
				
				if(var2answer[1].startsWith("<") && var2answer[1].endsWith(">")){
					//set it to uri
					var2answer[1] = var2answer[1].substring(1, var2answer[1].length()-1);
					varAnsw.add("type", new JsonPrimitive("uri"));
				}
				else {
					//use string
					varAnsw.add("type", new JsonPrimitive("literal"));
				}
				varAnsw.add("value", new JsonPrimitive(var2answer[1]));
				
				binding.add(var2answer[0].substring(1), varAnsw);
			}
			
			bindings.add(binding);		
			
		}
	}
	
	
	public static JsonObject qaldRdf2eqaldJson(Model rdf, String datasetId) {
		JsonObject root = new JsonObject();
		JsonObject dataset = new JsonObject();
		dataset.add("id", new JsonPrimitive(datasetId));
		root.add("dataset", dataset);
		
		JsonObject questions = qaldRdf2qaldJson(rdf);
		root.add("questions", questions.get("questions"));
		
		
		return root;
	}
	
	public static void qaldRdf2eqaldJson(InputStreamReader rdf, OutputStream json, String lang, String datasetId) {
		Model qaldAsRdf = ModelFactory.createDefaultModel();
		qaldAsRdf = qaldAsRdf.read(rdf, QALD_RDF_PRE, lang);
		JsonObject eqaldAsJson = qaldRdf2eqaldJson(qaldAsRdf, datasetId);
		try {
			json.write(eqaldAsJson.toString().getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
