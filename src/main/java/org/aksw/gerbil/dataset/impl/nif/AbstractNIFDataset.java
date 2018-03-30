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
package org.aksw.gerbil.dataset.impl.nif;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.RdfModelContainingDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.io.nif.AbstractNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.adapters.RDFReaderRIOT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public abstract class AbstractNIFDataset extends AbstractDataset implements RdfModelContainingDataset, InitializableDataset {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(AbstractNIFDataset.class);

    private List<Document> documents;
    private String name;
    private boolean hasBeenInitialized = false;
    private Model rdfModel;

    public AbstractNIFDataset(String name) {
        this.name = name;
    }

    /**
     * This method returns an opened InputStream from which the NIF data will be
     * read. If an error occurs while opening the stream, null should be
     * returned. <b>Note</b> that for closing the stream
     * {@link #closeInputStream(InputStream)} is called. If there are other
     * resources related to this stream that have to be closed, this method can
     * be overwritten to free these resources, too.
     * 
     * @return an opened InputStream or null if an error occurred.
     */
    protected abstract InputStream getDataAsInputStream();

    /**
     * This method returns the language of the NIF data, e.g.,
     * {@link Lang#TURTLE}.
     * 
     * @return the language of the NIF data
     */
    protected abstract Lang getDataLanguage();

    /**
     * This method is called for closing the input stream that has been returned
     * by {@link #getDataAsInputStream()}.If there are other resources related
     * to this stream that have to be closed, this method can be overwritten to
     * free these resources, too.
     * 
     * @param inputStream
     *            the input stream which should be closed
     */
    protected void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
        }
    }

    @Override
    public synchronized void init() throws GerbilException {
        if (hasBeenInitialized) {
            return;
        }
        Model nifModel = ModelFactory.createDefaultModel();
        // dataset = RDFDataMgr.loadModel(rdfpath);
        InputStream inputStream = getDataAsInputStream();
        if (inputStream == null) {
            throw new GerbilException("Couldn't get InputStream.", ErrorTypes.DATASET_LOADING_ERROR);
        }
        try {
            LocalNIFParser parser = new LocalNIFParser(this);
            documents = parser.parseNIF(inputStream, nifModel);
            // RDFDataMgr.read(nifModel, inputStream, getDataLanguage());
        } catch (Exception e) {
            throw new GerbilException("Exception while parsing dataset.", e, ErrorTypes.DATASET_LOADING_ERROR);
        } finally {
            closeInputStream(inputStream);
        }

        // if there are still triples available
        rdfModel = nifModel;

        hasBeenInitialized = true;
        LOGGER.info("{} dataset initialized", name);
    }

    public String getName() {
        if (!hasBeenInitialized) {
            throw new IllegalStateException(
                    "This dataset hasn't been initialized. Please call init() before using the dataset.");
        }
        return name;
    }

    @Override
    public int size() {
        if (!hasBeenInitialized) {
            throw new IllegalStateException(
                    "This dataset hasn't been initialized. Please call init() before using the dataset.");
        }
        return documents.size();
    }

    @Override
    public List<Document> getInstances() {
        if (!hasBeenInitialized) {
            throw new IllegalStateException(
                    "This dataset hasn't been initialized. Please call init() before using the dataset.");
        }
        return documents;
    }

    @Override
    public Model getRdfModel() {
        return rdfModel;
    }

    protected static class LocalNIFParser extends AbstractNIFParser {

        private AbstractNIFDataset languageSource;

        public LocalNIFParser(AbstractNIFDataset languageSource) {
            super("");
            this.languageSource = languageSource;
        }

        @Override
        protected Model parseNIFModel(InputStream is, Model nifModel) {
            RDFReaderRIOT rdfReader = new RDFReaderRIOT(languageSource.getDataLanguage());
            rdfReader.read(nifModel, is, "");
            return nifModel;
        }

        @Override
        protected Model parseNIFModel(Reader reader, Model nifModel) {
            RDFReaderRIOT rdfReader = new RDFReaderRIOT(languageSource.getDataLanguage());
            rdfReader.read(nifModel, reader, "");
            return nifModel;
        }

    }

}
