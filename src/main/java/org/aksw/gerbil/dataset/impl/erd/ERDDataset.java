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
import java.io.RandomAccessFile;

import java.nio.charset.Charset;
import java.nio.file.Paths;

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
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import org.apache.commons.io.IOUtils;

@Deprecated
public class ERDDataset extends AbstractDataset implements InitializableDataset {

    private static final String FREEBASE_URI = "https://www.googleapis.com/freebase";
    
    private String file_text;
    private String file_annotation;
    private List<Document> documents;
    
    public ERDDataset(String filetext, String fileannotation) {
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
    
    private String generateDocumentUri(String fileName) {
        
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(Paths.get(fileName).getFileName().toString());
        
        return builder.toString();
        
    }
    
    protected List<Document> loadDocuments(File textfile, File annotationfile) throws GerbilException {

        if (!textfile.exists()) {
            throw new GerbilException("The given text file (" + textfile.getAbsolutePath() + ") does not exist.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        if (!annotationfile.exists()) {
            throw new GerbilException("The given annotation file (" + annotationfile.getAbsolutePath() + ") does not exist.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        
        List<Document> docs = new ArrayList<>();
        String documentUri = generateDocumentUri(textfile.getAbsolutePath());
        
        Map<String, ERDTrec> textMap = new HashMap<>();
        String text_data = "";
        byte[] filedata = new byte[(int) textfile.length()];
        ERDTrec datatrec = null;
        RandomAccessFile raf;
        
        try {
            raf = new RandomAccessFile(textfile, "r");
            raf.seek(0);
            raf.readFully(filedata);
            text_data = new String(filedata);
            raf.close();
        } catch (IOException e) {
            throw new GerbilException("Exception while reading text file of dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }

        int error = 0;
        String[] text_split = text_data.split("\n");
        for (String line : text_split) {
            String[] line_part = line.split("\t");
            String key;
            
            if (line_part.length != 2) {
                error++;
                key = "ERROR " + error;
            } else {
                key = line_part[0];
            }
            
            datatrec = new ERDTrec(line, datatrec);
            textMap.put(key, datatrec);
        }
        
        BufferedReader reader = null;
        List<Marking> markings = new ArrayList<>();
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(annotationfile), Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {

                String[] line_split = line.split("\t");
                if (line_split.length != 5) continue;

                datatrec = textMap.get(line_split[0]);
                if (datatrec != null) {
                    int position = datatrec.getTextPosition(line_split[3]);
                    int length = line_split[3].length();
                    markings.add(new NamedEntity(position, length, FREEBASE_URI + line_split[2]));
                }
            }
            
        } catch (IOException e) {
                throw new GerbilException("Exception while reading annotation file of dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
                IOUtils.closeQuietly(reader);
        }
        
        docs.add(new DocumentImpl(text_data, documentUri, markings));
        
        return docs;
    }
    
}
