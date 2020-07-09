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
package org.aksw.gerbil.dataset.impl.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;

/**
 * Class to help parse all the XML files in a provided directory into NIF
 * documents
 * 
 * @author Nikit
 *
 */
public class GenericXMLDataset extends AbstractDataset implements InitializableDataset {

	protected List<Document> documents;
	protected String textsDirectory;
	protected CommonXMLTagDef tagDef;

	public GenericXMLDataset(String textsDirectory, String dsName, CommonXMLTagDef tagDef) throws GerbilException {
		this.textsDirectory = textsDirectory;
		this.name = dsName;
		this.tagDef = tagDef;
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
		this.documents = loadDocuments(new File(textsDirectory));
	}

	protected List<Document> loadDocuments(File textDir) throws GerbilException {
		if ((!textDir.exists()) || (!textDir.isDirectory())) {
			throw new GerbilException(
					"The given text directory (" + textDir.getAbsolutePath() + ") is not existing or not a directory.",
					ErrorTypes.DATASET_LOADING_ERROR);
		}
		CommonXMLParser parser = new CommonXMLParser();
		GenericResult parsedResult;
		List<Document> documents = new ArrayList<Document>();
		for (File dsFile : textDir.listFiles()) {
			// parse the annotation file
			try {
				parsedResult = parser.parseDSFile(dsFile, name, tagDef);
			} catch (Exception e) {
				throw new GerbilException("Couldn't parse given Dataset file (\"" + dsFile.getAbsolutePath() + "\".", e,
						ErrorTypes.DATASET_LOADING_ERROR);
			}
			documents.addAll(parsedResult.getDocuments());
		}
		return documents;
	}

}
