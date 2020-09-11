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
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableMap;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

public class GENIADataset extends AbstractDataset implements InitializableDataset {

    private static final String WORD_TAG = "w";
    private static final String SENTENCE_TAG = "sentence";
    private static final String MARKING_TAG = "cons";
    private static final String MARKING_TYPE_TAG = "sem";

    private static final Map<String, String> TYPE_MAP = ImmutableMap.<String, String>builder()
            .put("G#DNA", "http://id.nlm.nih.gov/mesh/D004247")
            .put("G#RNA", "http://id.nlm.nih.gov/mesh/D012313")
            .put("G#protein", "http://id.nlm.nih.gov/mesh/D011506")
            .put("G#cell_line", "http://id.nlm.nih.gov/mesh/D002478")
            .put("G#cell_type", "http://id.nlm.nih.gov/mesh/D002477")
            .build();

    private String file;
    private List<Document> documents;
    private int firstDocId;
    private int lastDocId;

    public GENIADataset(String file) {
        this(file, -1, -1);
    }

    public GENIADataset(String file, String firstDocId, String lastDocId) {
        this(file, Integer.parseInt(firstDocId), Integer.parseInt(lastDocId));
    }

    public GENIADataset(String file, int firstDocId, int lastDocId) {
        this.file = file;
        this.firstDocId = firstDocId;
        this.lastDocId = lastDocId;
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

    public static void main(String[] args) throws IOException, GerbilException {
        GENIADataset d = new GENIADataset("gerbil_data/datasets/genia/GENIAcorpus3.02.merged_test.xml");
        d.init();
        for (Document a : d.getInstances()) {
            System.out.println(a);
        }
        System.out.println(d.getInstances().size());
        d.close();
    }

    private List<Document> loadDocuments(File filePath) throws GerbilException {
        List<Document> docs = new ArrayList<>();
        String documentUriPrefix = "http://" + getName() + "/";
        StringBuilder currentText = new StringBuilder();
        List<Marking> currentMarkings = new ArrayList<>();
        Deque<TypedNamedEntity> markingStack = new ArrayDeque<TypedNamedEntity>();
        int index = 0;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(filePath));
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();

                // handle formatting error in dataset file, token "HMG-I(Y)" not marked as word in one sentence
                if (nextEvent.isCharacters() && nextEvent.asCharacters().getData().equals("HMG-I(Y)")) {
                    currentText.append("HMG-I(Y) ");
                } else if (nextEvent.isStartElement()) {
                    StartElement start = nextEvent.asStartElement();
                    String tag = start.getName().getLocalPart();
                    if (tag.equals(WORD_TAG)) {
                        nextEvent = reader.nextEvent();
                        currentText.append(nextEvent.asCharacters().getData() + " ");
                    } else if (tag.equals(MARKING_TAG)) {
                        markingStack.push(parseStartMarking(start, currentText));
                    }
                } else if(nextEvent.isEndElement()) {
                    String tag = nextEvent.asEndElement().getName().getLocalPart();
                    if(tag.equals(SENTENCE_TAG)) {
                        Document curDoc = new DocumentImpl(currentText.toString(), documentUriPrefix + index, currentMarkings);
                        docs.add(curDoc);
                        currentText.setLength(0);
                        ++index;
                        currentMarkings = new ArrayList<>();
                    } else if (tag.equals(MARKING_TAG))  {
                        TypedNamedEntity entity = markingStack.pop();
                        //skip higher level annotations that don't appear on the surface of the text
                        if(entity.getStartPosition() != -1) {
                            entity.setLength(currentText.length()-1-entity.getStartPosition());
                            currentMarkings.add(entity);
                        }
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        }
        return docs;
    }

    private TypedNamedEntity parseStartMarking(StartElement start, StringBuilder currentText) {
        TypedNamedEntity entity = new TypedNamedEntity(-1, -1, "", new HashSet<String>());
        Attribute type = start.getAttributeByName(new QName(MARKING_TYPE_TAG));
        // skip higher level annotations that don't appear on the surface of the text
        if (type != null) {
            String typeString = type.getValue();
            Optional<String> key = TYPE_MAP.keySet().stream().filter(typeString::contains).findFirst();
            if(key.isPresent()) {
                entity.setStartPosition(currentText.length());
                entity.getTypes().add(TYPE_MAP.get(key.get()));
            } 
        }
        return entity;
    }
}
