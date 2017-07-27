package org.aksw.gerbil.annotator.impl.sw;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

public class FileBasedRDFSystemTest {

	@Test
	public void checkLoading() throws GerbilException{
//		FileBasedRDFSystem system = new FileBasedRDFSystem();
		List<String> rdfFile = new ArrayList<String>();
		List<String> questionUriPrefix = new ArrayList<String>();
		
		rdfFile.add("src/test/resources/filetest.nt");
		questionUriPrefix.add("http://test.com/");
		
		List<Model> actual = FileBasedRDFSystem.loadInstances(rdfFile, questionUriPrefix);
		Model expected = ModelFactory.createDefaultModel();
		RDFDataMgr.read(expected, "src/test/resources/filetest.nt");
		assertTrue(expected.isIsomorphicWith(actual.get(0)));
	}
	
}
