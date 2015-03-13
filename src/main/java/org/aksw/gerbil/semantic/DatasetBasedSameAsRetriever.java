package org.aksw.gerbil.semantic;

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
