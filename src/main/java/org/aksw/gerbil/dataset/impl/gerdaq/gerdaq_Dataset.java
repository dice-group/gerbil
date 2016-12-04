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
package org.aksw.gerbil.dataset.impl.gerdaq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class gerdaq_Dataset extends AbstractDataset implements InitializableDataset {

    private static final String WIKIPEDIA_URI = "http://en.wikipedia.org/wiki/";
    private static final String ANNOTATION_TAG = "annotation";
    
    private String file;
    private List<Document> documents;
    private List<Marking> markings = new ArrayList<>();
    
    public gerdaq_Dataset(String file) {
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
    
    private String generateDocumentUri(String fileName) {
        
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(name);
        builder.append('/');
        builder.append(fileName);
        
        return builder.toString();
        
    }
    
    private List<Document> loadDocuments(File directory) throws GerbilException {
        
        List<Document> docs = new ArrayList<>();
        
        if ((!directory.exists()) || (!directory.isDirectory())) {
            throw new GerbilException("The given directory (" + directory.getAbsolutePath() + ") is not existing or not a directory.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        
        String directoryPath = directory.getAbsolutePath();
        if (!directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath + File.separator;
        }

        for (File tmpFile : new File(directoryPath).listFiles()){
            docs.add(createDocument(tmpFile));
        }
        
        return docs;
        
    }
    
    private Document createDocument(File file) throws GerbilException {
        
        String documentUri = generateDocumentUri(file.getName());
        BufferedReader reader = null;
        String text = "";
        
        if ((!file.exists()) || (file.isDirectory())) {
            throw new GerbilException("The given filepath (" + file.getAbsolutePath() + ") is not existing or not a file.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        
        try {
            createSAXparser(file);
            
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
                text = reader.toString();
            } catch (IOException e) {
                    throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
            } finally {
                    IOUtils.closeQuietly(reader);
            }
            
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            Logger.getLogger(gerdaq_Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<Marking> marks = new ArrayList<>();
        marks.addAll(markings);
        markings.clear();
        
        return new DocumentImpl(text, documentUri, marks);
        
    }
    
    private void createSAXparser(File file) throws SAXException, ParserConfigurationException, IOException, GerbilException {
        
        InputStream inputStream = new FileInputStream(file);
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        InputSource is = new InputSource(reader);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        
        try {
            saxParser.parse(is, createDefaultHandler());
        } catch (SAXParseException e) {
            throw new GerbilException("The loaded format from file " + file.getAbsolutePath() + " is not in UTF-8 format.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        
    }
    
    private DefaultHandler createDefaultHandler() {
        
        DefaultHandler handler = new DefaultHandler(){
            
            private Locator locator;
            private String tag;
            private long last_Locator_line;
            private long last_Locator_column;
            private long count_column;
            private long origintaglength;
            private List<String> title;
            private int countSpezialSymbols;
            
            @Override
            public void setDocumentLocator(Locator locator) {
                this.locator = locator;
            }

            @Override
            public void startDocument() throws SAXException {
                this.tag = "";
                this.origintaglength = -1;
                this.last_Locator_line = locator.getLineNumber();
                this.last_Locator_column = -1;
                this.count_column = 0;
                this.title = new LinkedList();
                this.countSpezialSymbols = 0;
                super.startDocument();
            }

            @Override
            public void endDocument() throws SAXException {
                this.tag = "";
                this.origintaglength = -1;
                this.last_Locator_line = -1;
                this.last_Locator_column = -1;
                this.count_column = 0;
                this.title.clear();
                this.countSpezialSymbols = 0;
                super.endDocument();
            }
            
            @Override
            public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

                if (qName.equals(ANNOTATION_TAG) && locator != null){
                    
                    for ( int i = 0; i < atts.getLength(); i++ ){
                        
                        if (Pattern.matches("rank_\\p{Digit}_title", atts.getQName(i))){
                        
                            String string = atts.getValue(i);

                            try {
                                byte[] utf8 = string.getBytes("UTF-8");
                                int count = 0;
                                boolean praNeg = false;
                                for (int z = 0; z < utf8.length - 1; z++){
                                    if (utf8[z]<0) {
                                        if (praNeg) count++;
                                        praNeg = true;
                                    } else {
                                        praNeg = false;
                                    }
                                }

                                countSpezialSymbols = countSpezialSymbols + count;

                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(gerdaq_Dataset.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            origintaglength = StringEscapeUtils.escapeHtml4(atts.getValue(i)).length();
                            tag = atts.getValue(i).replace(" ", "_");
                            title.add(tag);
                        
                        }
                    }
                }
            }
            
            @Override
            public void characters(char[] ch, int start, int length) {

                checkPosition();
                
                if (origintaglength > 0){

                    int calc = (int)(count_column + locator.getColumnNumber() - length + countSpezialSymbols/*- 3 - origintaglength*/);
                    
                    for (String tmp : title){
                        //markings.add(new NamedEntity(calc, (int)origintaglength, WIKIPEDIA_URI + tmp));
                        markings.add(new NamedEntity(calc, length, WIKIPEDIA_URI + tmp));
                    }
                    
                    origintaglength = -1;
                    title.clear();
                    
                }
                
            }
            
            private void checkPosition() {
                
                if (last_Locator_column < 0){
                    // <?xml version='1.0' encoding='UTF-8'?>
                    count_column = 38;
                    last_Locator_column = 0;
                }
                
                if (last_Locator_line < locator.getLineNumber()){
                    count_column = count_column + last_Locator_column;
                }
                
                last_Locator_line = locator.getLineNumber();
                last_Locator_column = locator.getColumnNumber();
            }
            
        };
        
        return handler;
        
    }
    
}
