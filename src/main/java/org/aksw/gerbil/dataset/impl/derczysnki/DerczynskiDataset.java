package org.aksw.gerbil.dataset.impl.derczysnki;

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
import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;

public class DerczynskiDataset extends AbstractDataset implements InitializableDataset {

    private static final char SEPARATION_CHAR = 0;
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
		BufferedReader bReader = null;
		CSVReader reader = null;
		List<Document> documents = new ArrayList<Document>();
		String documentUriPrefix = "http//:" + getName() + "/";
		try {
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(tweetsFile), Charset.forName("UTF-8")));
			reader = new CSVReader(bReader, SEPARATION_CHAR);

			String line[] = reader.readNext();
			int start=0, tweetIndex=0;
			List<Marking> markings = new ArrayList<Marking>();
			StringBuilder tweet = new StringBuilder("").append(line[0]);
			while (line != null) {
				if(line.length==0){
					//Get Markings
					findMarkings(tweet.toString());
					//Save old tweet
					documents.add(new DocumentImpl(tweet.toString(), documentUriPrefix
							+ tweetIndex, markings));
					//New Tweet 
					tweet.delete(0, tweet.length());
					tweetIndex++;
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
	
	public static List<Marking> findMarkings(String tweet){
		int start=0;
		List<Marking> markings = new ArrayList<Marking>();
		
		String[] line = tweet.split("\n");
		for(String tokenFull : line){
			String[] token = tokenFull.split("\t+");
			token[1]=token[1].trim();
			if(!token[1].trim().equals("O") && !token[1].trim().equals("NIL")){
				//TOken has URI
				markings.add(new NamedEntity(start, token[0].length(), token[1]));
			}
			start+=tokenFull.length()+1;
		}
		
		return markings;
	}


}
