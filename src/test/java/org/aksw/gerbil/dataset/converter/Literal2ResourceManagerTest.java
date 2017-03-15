package org.aksw.gerbil.dataset.converter;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.aksw.gerbil.dataset.converter.impl.SPARQLBasedLiteral2Resource;
import org.junit.Test;

public class Literal2ResourceManagerTest {


	@Test
	public void test(){
		Literal2ResourceManager m = new Literal2ResourceManager();
		assertTrue(m.getResourcesForLiteral("test").isEmpty());
		Literal2Resource converter = new SPARQLBasedLiteral2Resource("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org");
		m.registerLiteral2Resource(converter);
		Set<String> tmp = m.getResourcesForLiteral("Deutschland", "de");
		assertTrue(tmp.size()==1);
		assertTrue(tmp.iterator().next().equals("http://dbpedia.org/resource/Germany"));
		Literal2Resource converterEs = new SPARQLBasedLiteral2Resource("http://es.dbpedia.org/sparql");
		m.registerLiteral2Resource(converterEs);
		tmp = m.getResourcesForLiteral("Abel I de Dinamarca", "es");
		assertTrue(tmp.size()==2);
		assertTrue(tmp.contains("http://dbpedia.org/resource/Abel,_King_of_Denmark"));
		assertTrue(tmp.contains("http://es.dbpedia.org/resource/Abel_I_de_Dinamarca"));
	}
}
