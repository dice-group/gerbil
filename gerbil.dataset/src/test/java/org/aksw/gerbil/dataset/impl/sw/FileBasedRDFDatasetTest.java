package org.aksw.gerbil.dataset.impl.sw;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

public class FileBasedRDFDatasetTest {

	
	@Test
	public void rdfDatasetLoad() throws GerbilException, IOException{
		FileBasedRDFDataset dataset = new FileBasedRDFDataset("src/test/resources/filetest.nt");
		
		dataset.init();
		List<Model> actual = dataset.getInstances();
		dataset.close();
		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, "src/test/resources/filetest.nt");
		assertTrue(expected.isIsomorphicWith(actual.get(0)));
		
	}
	
}
