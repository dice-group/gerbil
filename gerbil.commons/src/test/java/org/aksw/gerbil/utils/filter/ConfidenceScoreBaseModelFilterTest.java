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
package org.aksw.gerbil.utils.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Assert;
import org.junit.Test;

public class ConfidenceScoreBaseModelFilterTest {

	private final String confidenceURI = "http://ontology.thomsonreuters.com/supplyChain#aggregatedConfidenceScore";
	private Property confidenceProp = ResourceFactory.createProperty(confidenceURI);
	
    @Test
    public void testAccepting() {
        ModelFilter<Model> filter = new ConfidenceScoreBasedModelFilter<Model>(1.0, 0.7, confidenceProp);

        System.out.println(this.getClass().getSimpleName()+" {Test MAX>Threshold: ...}");
        Model m = ModelFactory.createDefaultModel();
        
        Resource res = m.createResource("http://example.com/res1");
        assertTrue(filter.isEntityGood(res));
        System.out.println(this.getClass().getSimpleName()+" {Test MAX>Threshold: DONE}");

        System.out.println(this.getClass().getSimpleName()+" {Test MAX<Threshold: ...}");
        filter = new ConfidenceScoreBasedModelFilter<Model>(0.1, 0.7, confidenceProp);
        assertFalse(filter.isEntityGood(res));
        System.out.println(this.getClass().getSimpleName()+" {Test MAX<Threshold: DONE}");

        System.out.println(this.getClass().getSimpleName()+" {Test value<Threshold: ...}");
        res.addLiteral(confidenceProp, 0.67);
        assertFalse(filter.isEntityGood(res));
        System.out.println(this.getClass().getSimpleName()+" {Test value<Threshold: DONE}");

        System.out.println(this.getClass().getSimpleName()+" {Test value>Threshold: ...}");
        res.addLiteral(confidenceProp, 0.87);
        assertTrue(filter.isEntityGood(res));
        System.out.println(this.getClass().getSimpleName()+" {Test value>Threshold: DONE}");
        
        System.out.println(this.getClass().getSimpleName()+" {Test value=Threshold: ...}");
        res.addLiteral(confidenceProp, 0.7);
        assertFalse(filter.isEntityGood(res));
        System.out.println(this.getClass().getSimpleName()+" {Test value=Threshold: DONE}");

    }
    
    @Test
    public void testModelAccepting() {
        ModelFilter<Model> filter = new ConfidenceScoreBasedModelFilter<Model>(1.0, 0.7, confidenceProp);
        Model testModel = ModelFactory.createDefaultModel();
        Resource res1 = ResourceFactory.createResource("http://example.com/res1");
        testModel.addLiteral(res1, this.confidenceProp, 0.8);
        Resource res2 = ResourceFactory.createResource("http://example.com/res2");
        testModel.addLiteral(res2, this.confidenceProp, 0.6);
        Resource res3 = ResourceFactory.createResource("http://example.com/res3");
        testModel.add(res3, ResourceFactory.createProperty("http://test.com/prop/a"), "NONSENSE");
        List<List<Model>> filterList = filter.filter2ListOfLists(testModel);
        assertEquals(1, filterList.size());
        assertEquals(1, filterList.get(0).size());
        Model filteredModel = filterList.get(0).get(0);
        assertEquals(2, filteredModel.size());
        List<Resource> resourceList = filteredModel.listSubjects().toList();
        assertTrue(resourceList.contains(res1));
        assertFalse(resourceList.contains(res2));
        assertTrue(resourceList.contains(res3));
    }

}
