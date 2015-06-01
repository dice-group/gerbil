/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.semantic.sameas;

import org.aksw.gerbil.dataset.Dataset;
import org.aksw.gerbil.dataset.RdfModelContainingDataset;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.OWL;

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
