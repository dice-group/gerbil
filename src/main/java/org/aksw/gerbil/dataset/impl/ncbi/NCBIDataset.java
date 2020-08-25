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
package org.aksw.gerbil.dataset.impl.ncbi;

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
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.io.IOUtils;

/**
 * Dataset Adapter for the NCBI Disease Corpus
 */
public class NCBIDataset extends AbstractDataset implements InitializableDataset {

	private static final int ENTITY_START_COLUMN = 1; 
	private static final int ENTITY_END_COLUMN = 2; 
	private static final int ENTITY_URI_COLUMN = 5; 
	private static final String MESH_PREFIX = "http://id.nlm.nih.gov/mesh/";
	private static final String OMIM_PREFIX = "https://omim.org/entry/";

    protected String file;
    protected List<Document> documents;

    public NCBIDataset(String file) {
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
    }

    protected List<Document> loadDocuments(File file) throws GerbilException {
		BufferedReader reader = null;
		List<Document> documents = new ArrayList<Document>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			StringBuilder currentDoc = new StringBuilder("");
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() && currentDoc.length() > 0) {
					documents.add(getDocument(currentDoc.toString().trim()));
					currentDoc.delete(0, currentDoc.length());
				} else {
					currentDoc.append(line + "\n");
				}
			}
			//check if there is a document to be added
			if(currentDoc.length() > 0) {
				documents.add(getDocument(currentDoc.toString().trim()));
			}
		} catch (IOException e) {
			throw new GerbilException("Exception while reading dataset.", e,
					ErrorTypes.DATASET_LOADING_ERROR);
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return documents;
	}

	private Document getDocument(String currentDoc) {
		String documentUriPrefix = "http://" + getName() + "/";
		String[] lines = currentDoc.split("\n");
		String[] title = lines[0].split("\\|");
		String[] text = lines[1].split("\\|");

		List<Marking> markings = new ArrayList<>();
		for(int i = 2; i<lines.length; i++) {
			String[] tokens = lines[i].split("\t");
			int start = Integer.parseInt(tokens[ENTITY_START_COLUMN]);
			int length = Integer.parseInt(tokens[ENTITY_END_COLUMN]) - start;
			markings.add(new NamedEntity(start, length, parseURIs(tokens[ENTITY_URI_COLUMN])));
		}
		return new DocumentImpl(title[2] + "\n" + text[2], documentUriPrefix + title[0], markings);
	}

	private Set<String> parseURIs(String tokens) {
		Set<String> uris = new HashSet<>();
		String[] annotations = tokens.split("[\\+\\|]");
		for(int i = 0; i<annotations.length; i++) {
			//fix some formatting issues
			String currentUri = annotations[i].trim();
			if(currentUri.startsWith("D") || currentUri.startsWith("C")) {
				uris.add(MESH_PREFIX + currentUri);	
			} else if (currentUri.startsWith("M")) { 
				uris.add(MESH_PREFIX + currentUri.substring(currentUri.indexOf(":") +  1));	
			} else {
				uris.add(OMIM_PREFIX + currentUri.substring(currentUri.indexOf(":") +  1));	
			}
		}
		return uris;
	}
}