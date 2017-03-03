package org.aksw.gerbil.dataset.impl.wsdm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.Annotation;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.utils.WikipediaHelper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSDMDataset extends AbstractDataset implements
						InitializableDataset {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WSDMDataset.class);
	
	private static String WIKIPEDIA_DOMAIN = "en.wikipedia.org";
	protected List<Document> documents;
	private String annotatedFile;
	private String tweetsFile;

	public WSDMDataset(String annotatedFile, String tweetsFile){
		this.annotatedFile = annotatedFile;
		this.tweetsFile = tweetsFile;
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
		this.documents = loadDocuments(new File(annotatedFile), new File(tweetsFile));
	}

	private List<Document> loadDocuments(File annotations, File tweets)
			throws GerbilException {
		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http://" + getName() + "/";
		//its json per line 
		try (BufferedReader bReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(tweets), Charset.forName("UTF-8")))) {
			String line;
			List<Marking> markings;
			while ((line = bReader.readLine()) != null) {
				JSONObject json = new JSONObject(line);
				
				String id = json.getString("id_str");
				String text = json.getString("text");
				markings = findMarkings(getMarkingLines(annotations, id), text);
				documents.add(new DocumentImpl(text, documentUriPrefix + id,
						markings));
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		}

		return documents;
	}
	
	protected static List<Marking> findMarkings(Set<String> lines, String text) {
		List<Marking> markings = new ArrayList<Marking>();

		for (String line : lines) {
			String[] annotation = line.split("\t");

			String uri = WikipediaHelper.getWikipediaUri(WIKIPEDIA_DOMAIN , annotation[2]);
			markings.add(new Annotation(uri));
		}

		return markings;
	}

	private static Set<String> getMarkingLines(File annotations, String id) {
		Set<String> lines = new HashSet<String>();

		try (BufferedReader bReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(annotations), Charset.forName("UTF-8")))) {
			String line;
			Boolean annotationSeen = false;
			while ((line = bReader.readLine()) != null) {
				String[] annotation = line.split("\t");
				if (id.equals(annotation[0])) {
					annotationSeen = true;
					lines.add(line);
				} else if (annotationSeen) {
					// as the annotations are ordered by id, the last annotation
					// was added
					return lines;
				}
			}

		} catch (IOException e) {
			LOGGER.error("Could not find Markings due to ", e);
		}
		return lines;
	}
	
}
