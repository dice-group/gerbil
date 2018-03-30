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
package org.aksw.gerbil.semantic.sameas.impl.model;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.RdfModelContainingDataset;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.OWL;

public class DatasetBasedSameAsRetriever extends RDFModelBasedSameAsRetriever {

    /**
     * Returns a DatasetBasedSameAsRetriever or null if the dataset does not
     * contain any owl:sameAs relations.
     * 
     * @param dataset
     * @return
     */
    public static DatasetBasedSameAsRetriever create(Dataset dataset) {
        if (dataset instanceof RdfModelContainingDataset) {
            RdfModelContainingDataset rdfDataset = (RdfModelContainingDataset) dataset;
            Model model = rdfDataset.getRdfModel();
            if (model == null) {
                return null;
            }
            if (model.contains(null, OWL.sameAs)) {
                return new DatasetBasedSameAsRetriever(model, rdfDataset);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private RdfModelContainingDataset dataset;

    protected DatasetBasedSameAsRetriever(Model model, RdfModelContainingDataset dataset) {
        super(model);
        this.dataset = dataset;
    }
}
