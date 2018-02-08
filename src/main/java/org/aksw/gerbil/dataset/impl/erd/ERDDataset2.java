package org.aksw.gerbil.dataset.impl.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;

public class ERDDataset2 extends AbstractDataset implements
		InitializableDataset {

	private List<Document> documents;
	private String annotateFile;
	private String textFile;
	
    private String queryTemp = "PREFIX owl:<http://www.w3.org/2002/07/owl#> PREFIX freebase:<http://rdf.freebase.com/ns/>  SELECT ?s WHERE {?s owl:sameAs freebase:%%v%%}";
	private static final String DBPEDIA_SERVICE = "http://dbpedia.org/sparql";


	public ERDDataset2(String textFile, String annotateFile) {
		this.annotateFile = annotateFile;
		this.textFile = textFile;
	}

	@Override
	public int size() {
		return documents.size();
	}

	@Override
	public List<Document> getInstances() {
		return documents;
	}

	@Override
	public void init() throws GerbilException {
		this.documents = loadDocuments(new File(annotateFile), new File(
				textFile));
	}

	private List<Document> loadDocuments(File annFile, File textFile) throws GerbilException {
		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http://" + getName() + "/";
		try (BufferedReader breader = new BufferedReader(new InputStreamReader(
				new FileInputStream(textFile), Charset.forName("UTF-8")))) {
			String line;
			List<Marking> markings = null;
			while ((line = breader.readLine()) != null) {
				if(line.isEmpty()){
					continue;
				}
				String[] text = line.split("\t");

				markings = findMarkings(text, annFile);
				documents.add(new DocumentImpl(text[1], documentUriPrefix
						+ text[0], markings));
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		}

		return documents;
	}

	private List<Marking> findMarkings(String[] text, File annFile) throws GerbilException {
		List<Marking> markings = new ArrayList<Marking>();
		try (BufferedReader breader = new BufferedReader(new InputStreamReader(
				new FileInputStream(annFile), Charset.forName("UTF-8")))) {
			String line;
			
			while ((line = breader.readLine()) != null) {
				if(line.isEmpty()){
					continue;
				}
				String[] annotation = line.split("\t");
				int searchID = getTrecID(text[0]);
				int annoID = getTrecID(annotation[0]);
				if(searchID == annoID){
					int start = text[1].indexOf(annotation[3]);
					int length = annotation[3].length();
					
					//FIXME time consuming!
                    String freebaseID = annotation[2].substring(1, annotation[2].length()).replace("/",".");
                    Query query = QueryFactory.create(queryTemp.replace("%%v%%", freebaseID));
                    QueryExecution qexec = QueryExecutionFactory.createServiceRequest(DBPEDIA_SERVICE, query);
                    String uri =  qexec.execSelect().next().getResource("s").getURI();
                    
					
					markings.add(new NamedEntity(start, length, uri));
				}
				else if(annoID > searchID){
					//There is no annotation for the given text
					break;
				}
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		}

		return markings;
	}

	private int getTrecID(String trec){
		return Integer.valueOf(trec.replace("TREC-", ""));
	}
	
}
