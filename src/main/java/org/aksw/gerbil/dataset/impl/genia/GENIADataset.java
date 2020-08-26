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
package org.aksw.gerbil.dataset.impl.genia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;

public class GENIADataset extends AbstractDataset implements InitializableDataset {

    private static final String WORD_TAG = "w";
    private static final String SENTENCE_TAG = "sentence";
    private static final String MARKING_TAG = "cons";
    private static final String MARKING_TYPE_TAG = "sem";

    private String file;
    private List<Document> documents;

    public GENIADataset(String file) {
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

    private List<Document> loadDocuments(File filePath) throws GerbilException {
        List<Document> docs = new ArrayList<>();
        String documentUriPrefix = "http://" + getName() + "/";
        StringBuilder currentText = new StringBuilder();
        List<Marking> currentMarkings = new ArrayList<>();
        Deque<Integer> markingStack = new ArrayDeque<Integer>();
        int index = 0;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(filePath));
            while(reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();

                //handle formatting error in dataset file, token "HMG-I(Y)" not marked as word in one sentence 
                if(nextEvent.isCharacters() && nextEvent.asCharacters().getData().equals("HMG-I(Y)")) {
                    currentText.append("HMG-I(Y) ");
                }

                if(nextEvent.isStartElement()) {
                    StartElement start = nextEvent.asStartElement();
                    String tag = start.getName().getLocalPart();
                    if(tag.equals(WORD_TAG)) {
                        nextEvent = reader.nextEvent();
                        currentText.append(nextEvent.asCharacters().getData() + " ");
                    } else if (tag.equals(MARKING_TAG)) {
                        Attribute type = start.getAttributeByName(new QName(MARKING_TYPE_TAG));
                        //skip higher level annotations that don't appear on the surface of the text
                        markingStack.push((type != null) ? currentText.length() : -1);
                    }
                }

                if(nextEvent.isEndElement()) {
                    String tag = nextEvent.asEndElement().getName().getLocalPart();
                    if(tag.equals(SENTENCE_TAG)) {
                        Document curDoc = new DocumentImpl(currentText.toString(), documentUriPrefix + index, currentMarkings);
                        docs.add(curDoc);
                        currentText.setLength(0);
                        ++index;
                        currentMarkings = new ArrayList<>();
                    } else if (tag.equals(MARKING_TAG))  {
                        int start = markingStack.pop();
                        //skip higher level annotations that don't appear on the surface of the text
                        if(start != -1) {
                            NamedEntity marking = new NamedEntity(start, currentText.length()-1-start, "");
                            currentMarkings.add(marking);
                        }
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        return docs;
    }
}
