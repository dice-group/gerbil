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
package org.aksw.gerbil.dataset.impl.erd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

import org.apache.commons.io.IOUtils;

public class erd_Dataset extends AbstractDataset implements InitializableDataset {

    private String file_text;
    private String file_annotation;
    private List<Document> documents;
    
    public erd_Dataset(String filetext, String fileannotation) {
        this.file_text = filetext;
        this.file_annotation = fileannotation;
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
        this.documents = loadDocuments(new File(file_text), new File(file_annotation));
    }
    
    protected List<Document> loadDocuments(File textfile, File annotationfile) throws GerbilException {

        if (!textfile.exists()) {
            throw new GerbilException("The given text file (" + textfile.getAbsolutePath() + ") does not exist.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        if (!annotationfile.exists()) {
            throw new GerbilException("The given annotation file (" + annotationfile.getAbsolutePath() + ") does not exist.",
                    ErrorTypes.DATASET_LOADING_ERROR);
        }
        List<Document> docs = new ArrayList<>();
        Map<String, String> textAnnotationsMap = new HashMap<>();
        List<Marking> markings = new ArrayList<>();
        BufferedReader reader = null;
        try {
                reader = new BufferedReader(new InputStreamReader(
                                new FileInputStream(textfile), Charset.forName("UTF-8")));

                String line = reader.readLine();
                while (line != null) {
                    if(!line.trim().isEmpty()){
                        //
                    }
                    line = reader.readLine();
                }
        } catch (IOException e) {
                throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
                IOUtils.closeQuietly(reader);
        }
        
        return docs;
    }
}
