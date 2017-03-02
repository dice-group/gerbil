/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.dataset.impl.micro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.io.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Based Upon Micrposts2014Dataset
 * 
 * @author Felix Conrads (conrads@informatik.uni-leipzig.de)
 * 
 *         Microposts2014Dataset:
 * @author Giuseppe Rizzo (giuse.rizzo@gmail.com)
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class Microposts2013Dataset extends AbstractDataset implements
		InitializableDataset {
	//
	// private static final Logger LOGGER = LoggerFactory
	// .getLogger(Microposts2013Dataset.class);

	private static final char SEPARATION_CHAR = '\t';
	private static final String SEPARATION_CHAR_ANNOTATION = ";";
	private static final int TWEET_ID_INDEX = 0;
	private static final int TWEET_TEXT_INDEX = 2;
	private static final int ANNOTATION_INDEX = 1;

	private static final String HASHTAG = "_HASHTAG_";

	protected List<Document> documents;
	private String tweetsFile;

	public Microposts2013Dataset(String tweetsFile) {
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
		this.documents = loadDocuments(new File(tweetsFile));
	}

	protected List<Document> loadDocuments(File tweetsFile)
			throws GerbilException {
		BufferedReader bReader = null;
		CSVReader reader = null;
		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http://" + getName() + "/";
		try {
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(tweetsFile), Charset.forName("UTF-8")));
			reader = new CSVReader(bReader, SEPARATION_CHAR);

			String line[] = reader.readNext();
			String text;
			int start, end;
			List<Marking> markings = null;
			while (line != null) {
				if (line.length == 3) {

					start = line[TWEET_TEXT_INDEX].startsWith("\"") ? 1 : 0;
					end = line[TWEET_TEXT_INDEX].endsWith("\"") ? (line[TWEET_TEXT_INDEX]
							.length() - 1) : line[TWEET_TEXT_INDEX].length();
					text = line[TWEET_TEXT_INDEX].substring(start, end).trim();
					markings = findMarkings(line[ANNOTATION_INDEX], text);
					text = text.replaceAll(HASHTAG + " ", "#");
					text = text.replaceAll(HASHTAG, "#");
					documents.add(new DocumentImpl(text, documentUriPrefix
							+ line[TWEET_ID_INDEX], markings));
				} else {
					throw new GerbilException(
							"Dataset is malformed. Each line shoud have an exactly 3 cells. Malformed line = "
									+ Arrays.toString(line),
							ErrorTypes.DATASET_LOADING_ERROR);
				}

				line = reader.readNext();
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(bReader);
		}
		return documents;
	}

	protected static List<Marking> findMarkings(String line, String text) {
		String[] annotations = line.split(SEPARATION_CHAR_ANNOTATION);

		List<Marking> markings = new ArrayList<Marking>(annotations.length);
		if (line.isEmpty()) {
			return markings;
		}
		int start;
		int end = 0;

		Set<String> types = new HashSet<String>();

		for (int i = 0; i < annotations.length; i++) {
			int offset = 0;
			String annotation = null;
			// Annotations has the form PER/PersonName etc.
			annotations[i] = annotations[i].replace("MISC:", "MISC/");
			if (annotations[i].contains("/")) {
				String type = annotations[i].split("/")[0];
				switch (type) {
				case "PER":
					types.add("http://dbpedia.org/ontology/Person");
					break;
				case "ORG":
					types.add("http://dbpedia.org/ontology/Organisation");
					break;
				case "LOC":
					types.add("http://dbpedia.org/ontology/Place");
					break;
				case "MISC":
					// TODO
				}
				text = text.replaceAll(HASHTAG + " ", "#");
				text = text.replaceAll(HASHTAG, "#");

				try {
					annotation = annotations[i].split("/")[1];
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				annotation = annotations[i];
			}
			start = text.indexOf(annotation, end);

			// check if mention has a hashtag before
			if (start > 0 && text.substring(start - 1, start).equals("#")) {
				start -= 1;
				offset = 1;
			}

			end = start + annotation.length() + offset;

			markings.add(new TypedNamedEntity(start, end - start, "", types));
		}
		return markings;
	}
}
