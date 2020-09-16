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
package org.aksw.gerbil.dataset.impl.voxel;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.dataset.impl.nif.FileBasedNIFDataset;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.compress.utils.IOUtils;

public class VoxELDataset extends AbstractDataset implements InitializableDataset {

	protected List<Document> documents;
	protected String file;

	public VoxELDataset(String file) {
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
		this.documents = loadDocuments();
	}

	protected List<Document> loadDocuments() throws GerbilException {
		FileBasedNIFDataset nifDataset = new FileBasedNIFDataset(file);
		List<Document> documents = new ArrayList<Document>();
		try {
			nifDataset.init();
			List<Document> docs = nifDataset.getInstances();
			// the datasets also contain the complete articles as nif:Context, from which 
			// the annotated sentences are taken, these need to be removed
			for(Document d1: docs) {
				String sentence = d1.getText();
				for(Document d2: docs) {
					if(d2.getText().contains(sentence) && !d2.equals(d1))  {
						documents.add(d1);
						break;
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(nifDataset);
		}
		return documents;
	}
}
