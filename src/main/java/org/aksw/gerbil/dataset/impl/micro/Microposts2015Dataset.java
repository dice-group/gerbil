package org.aksw.gerbil.dataset.impl.micro;

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
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Microposts2015Dataset extends AbstractDataset implements
		InitializableDataset {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Microposts2016Dataset.class);

	protected List<Document> documents;
	private String annotatedFile;
	private String tweetsFile;

	protected static int typeIndex = 4;

	public Microposts2015Dataset(String annotatedFile, String tweetsFile) {
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
		this.documents = loadDocuments(new File(annotatedFile), new File(
				tweetsFile));
	}

	protected List<Document> loadDocuments(File annotations, File tweetsFile)
			throws GerbilException {

		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http://" + getName() + "/";

		try (BufferedReader bReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(tweetsFile), Charset.forName("UTF-8")))) {
			String line;
			List<Marking> markings;
			while ((line = bReader.readLine()) != null) {
				String[] tweet = line.split("\t");
				if (tweet.length < 2) {
					continue;
				}
				String id = tweet[0];
				String text = tweet[1];
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
			if(annotation.length<3)
				continue;
			
			int start = Integer.parseInt(annotation[1]);
			int end = Integer.parseInt(annotation[2]);
			int length = end - start;
			String uri = annotation[3];
			if (uri.startsWith("NIL")) {
				uri = "";
			}
			Set<String> types = new HashSet<String>();
			types.add(getTypeURI(annotation[typeIndex]));

			markings.add(new TypedNamedEntity(start, length, uri, types));

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

	protected static String getTypeURI(String type) {
		switch (type.toLowerCase()) {
		case "thing":
			return "http://dbpedia.org/ontology/Thing";
		case "person":
			return "http://dbpedia.org/ontology/Person";
		case "organization":
			return "http://dbpedia.org/ontology/Organisation";
		case "location":
			return "http://dbpedia.org/ontology/Place";
		case "event":
			return "http://dbpedia.org/ontology/Event";
		case "product":
			return "http://dbpedia.org/ontology/Product";
		}
		return "";
	}
}
