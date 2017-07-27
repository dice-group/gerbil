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
package org.aksw.gerbil.dataset.impl.sw;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.dataset.RdfModelContainingDataset;
import org.aksw.gerbil.dataset.impl.AbstractDataset;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRDFDataset extends AbstractDataset implements RdfModelContainingDataset, InitializableDataset {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(AbstractRDFDataset.class);

    private List<Model> models = new ArrayList<Model>();
    private String name;
    private boolean hasBeenInitialized = false;

    public AbstractRDFDataset(String name) {
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
    protected abstract String getFileName();

   

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
        Model model = ModelFactory.createDefaultModel();
        RDFDataMgr.read(model, getFileName());
        models.add(model);
        // if there are still triples available
        //rdfModel = model;

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
        return models.size();
    }

    @Override
    public List<Model> getInstances() {
        if (!hasBeenInitialized) {
            throw new IllegalStateException(
                    "This dataset hasn't been initialized. Please call init() before using the dataset.");
        }
        return models;
    }



}
