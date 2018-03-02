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
package org.aksw.gerbil.dataset.impl.micro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntArrayList;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Giuseppe Rizzo (giuse.rizzo@gmail.com)
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class Microposts2014Dataset extends AbstractDataset implements InitializableDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(Microposts2014Dataset.class);

    private static final char SEPARATION_CHAR = '\t';
    private static final int TWEET_ID_INDEX = 0;
    private static final int TWEET_TEXT_INDEX = 1;
    private static final int FIRST_ANNOTATION_INDEX = 2;

    protected List<Document> documents;
    private String tweetsFile;

    public Microposts2014Dataset(String tweetsFile) {
        this.tweetsFile = tweetsFile;
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
        this.documents = loadDocuments(new File(tweetsFile));
    }

    protected List<Document> loadDocuments(File tweetsFile) throws GerbilException {
        BufferedReader bReader = null;
        CSVReader reader = null;
        List<Document> documents = new ArrayList<Document>();
        String documentUriPrefix = "http://" + getName() + "/";
        try {
            bReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(tweetsFile), Charset.forName("UTF-8")));
            reader = new CSVReader(bReader, SEPARATION_CHAR);

            String line[] = reader.readNext();
            String text;
            int start, end;
            List<Marking> markings;
            while (line != null) {
                if ((line.length & 1) == 0) {
                    start = line[TWEET_TEXT_INDEX].startsWith("\"") ? 1 : 0;
                    end = line[TWEET_TEXT_INDEX].endsWith("\"") ? (line[TWEET_TEXT_INDEX].length() - 1)
                            : line[TWEET_TEXT_INDEX].length();
                    text = line[TWEET_TEXT_INDEX].substring(start, end).trim();
                    markings = findMarkings(line, text);
                    documents.add(new DocumentImpl(text, documentUriPrefix + line[TWEET_ID_INDEX], markings));
                } else {
//                    throw new GerbilException(
//                            "Dataset is malformed. Each line shoud have an even number of cells. Malformed line = "
//                                    + Arrays.toString(line),
//                            ErrorTypes.DATASET_LOADING_ERROR);
                	//ignore line!
                	LOGGER.debug("Each line should have an even number of cells. Ignoring this line "+Arrays.toString(line));
                }

                line = reader.readNext();
            }
        } catch (IOException e) {
            throw new GerbilException("Exception while reading dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(bReader);
        }
        return documents;
    }

    protected static List<Marking> findMarkings(String line[], String text) {
        List<Marking> markings = new ArrayList<Marking>(line.length / 2);
        String textWithoutHashes = null;
        int start, pos;
        IntArrayList hashes = new IntArrayList();
        int end = 0;
        for (int i = FIRST_ANNOTATION_INDEX; i < line.length; i = i + 2) {
            start = text.indexOf(line[i], end);
            // The mentioned entity couldn't be found. Let's search
            // in a text that contains no hashes.
            if (start < 0) {
                if (textWithoutHashes == null) {
                    /*
                     * A very simple workaround to search for a mention without
                     * hashes. Note that this only works, if the mention
                     * couldn't be found because the tweets contains hash tags
                     * that should be part of the mentions.
                     */
                    pos = text.indexOf('#');
                    while (pos >= 0) {
                        hashes.add(pos);
                        pos = text.indexOf('#', pos + 1);
                    }
                    textWithoutHashes = text.replaceAll("#", "");
                }
                // The offset might have been moved through the
                // removing
                // of the hashes.
                for (int j = 0; (i < hashes.elementsCount) && (hashes.buffer[j] < end); ++j) {
                    --end;
                }
                // search again
                start = textWithoutHashes.indexOf(line[i], end);
                if (start >= 0) {
                    // find the start and end positions of the
                    // mention
                    // inside the original tweet by looking at the
                    // list
                    // of hashes
                    end = start + line[i].length();
                    for (int j = 0; (j < hashes.elementsCount) && (hashes.buffer[j] < end); ++j) {
                        ++end;
                        if (hashes.buffer[j] < start) {
                            ++start;
                        }
                    }
                }
            } else {
                end = start + line[i].length();
            }
            if (start < 0) {
                LOGGER.warn("Couldn't find \"{}\" inside \"{}\". This annotation will be ignored.", line[i], text);
            } else {
                markings.add(new NamedEntity(start, end - start, line[i + 1]));
            }
        }
        return markings;
    }

}
