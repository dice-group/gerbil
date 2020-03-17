package org.aksw.gerbil.dataset.impl.derczysnki;

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
import org.apache.commons.io.IOUtils;

public class DerczynskiDataset extends AbstractDataset implements
		InitializableDataset {

	private static StringBuilder realTweet;
	private String file;
	private List<Document> documents;
	private int firstDocId;
	private int lastDocId;

	public DerczynskiDataset(String file) {
		this.file = file;
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
		this.documents = loadDocuments(new File(file));
		if ((firstDocId > 0) && (lastDocId > 0)) {
			this.documents = this.documents.subList(firstDocId - 1, lastDocId);
		}
	}

	protected List<Document> loadDocuments(File tweetsFile)
			throws GerbilException {
		BufferedReader reader = null;
		// CSVReader reader = null;
		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http://" + getName() + "/";
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(tweetsFile), Charset.forName("UTF-8")));

			String line = reader.readLine();
			int tweetIndex = 0;
			List<Marking> markings = new ArrayList<Marking>();
			StringBuilder tweet = new StringBuilder("");
			while (line != null) {
				if (line.trim().isEmpty()) {
					// Get Markings
					markings = findMarkings(tweet.toString());
					// Save old tweet
					documents.add(new DocumentImpl(realTweet.toString(),
							documentUriPrefix + tweetIndex, markings));
					// New Tweet
					tweet.delete(0, tweet.length());
					line = reader.readLine();
					tweetIndex++;
					continue;
				}
				tweet.append(line + "\n");
				line = reader.readLine();
			}
			//check if there is a tweet to be added
			if(tweet.length() > 0) {
				// Get Markings
				markings = findMarkings(tweet.toString());
				// Save last tweet
				documents.add(new DocumentImpl(realTweet.toString(),
						documentUriPrefix + tweetIndex, markings));
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		} finally {
			IOUtils.closeQuietly(reader);
			// IOUtils.closeQuietly(bReader);
		}
		return documents;
	}

	public static List<Marking> findMarkings(String tweet) {
		int start = 0;
		List<Marking> markings = new ArrayList<Marking>();
		realTweet = new StringBuilder();
		String[] line = tweet.split("\n");
		int i = 0;
		for (String tokenFull : line) {
			String[] token = tokenFull.split("\t+");
			realTweet.append(token[0] + " ");
			token[1] = token[1].trim();
			if (token.length>2&&token[2].startsWith("B-")) {
				String[] marking = getWholeMarking(line, i);
				Set<String> types = new HashSet<String>();
				types.add(marking[2]);
				markings.add(new TypedNamedEntity(start, marking[0].length(),
						marking[1], types));

			}
			start += token[0].length() + 1;
			i++;
		}

		return markings;
	}

	private static String[] getWholeMarking(String line[], int pos) {
		String[] ret = new String[3];
		String[] token = line[pos].split("\t+");
		StringBuilder name = new StringBuilder().append(token[0]);
		if (!token[1].equals("O") & !token[1].equals("") && !token[1].equals("NIL"))
			ret[1] = token[1];
		else
			ret[1] = "";
		ret[2] = getType(token[2].substring(2));
		for (int i = pos + 1; i < line.length; i++) {
			token = line[i].split("\t+");
			
			if (token.length >2 && token[2].startsWith("I-")) {
				name.append(" ").append(token[0]);
			} else {
				break;
			}
		}
		ret[0] = name.toString();
		return ret;
	}

	private static String getType(String type) {
		switch (type) {
		case "sportsteam":
			return "http://dbpedia.org/ontology/SportsTeam";
		case "person":
			return "http://dbpedia.org/ontology/Person";
		case "geo-loc":
			return "http://dbpedia.org/ontology/Place";
		case "facility":
			return "http://dbpedia.org/ontology/Place";
		case "movie":
			return "http://dbpedia.org/ontology/Film";
		case "tv-show":
			return "http://dbpedia.org/ontology/TelevisionShow";
		case "company":
			return "http://dbpedia.org/ontology/company";
		case "product":
			return "http://dbpedia.org/ontology/product";
		default:
			return "";
		}
	}

}
