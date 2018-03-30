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

import java.util.Arrays;
import java.util.HashSet;

import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Assert;
import org.junit.Test;

import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class TypeBasedMarkingFilterTest {

    public static final String ACCEPTED_TYPES[] = new String[] { RDFS.Class.getURI(), OWL.Class.getURI() };

    @Test
    public void testAccepting() {
        MarkingFilter<TypedMarking> filter = new TypeBasedMarkingFilter<>(true, ACCEPTED_TYPES);

        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Class",
                        "http://www.w3.org/2000/01/rdf-schema#Class")))));
        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Class")))));
        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2000/01/rdf-schema#Class")))));

        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(0, 20,
                "http://dbpedia.org/resource/Florence_May_Harding", new HashSet<String>(Arrays.asList(
                        "http://www.w3.org/2002/07/owl#Individual",
                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))));
        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(44, 6, "http://dbpedia.org/resource/Sydney",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                        "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location")))));
        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(61, 21,
                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))));
    }

    @Test
    public void testDeclining() {
        MarkingFilter<TypedMarking> filter = new TypeBasedMarkingFilter<>(false, ACCEPTED_TYPES);

        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(0, 20,
                "http://dbpedia.org/resource/Florence_May_Harding", new HashSet<String>(Arrays.asList(
                        "http://www.w3.org/2002/07/owl#Individual",
                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))));
        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(44, 6, "http://dbpedia.org/resource/Sydney",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                        "http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#Location")))));
        Assert.assertTrue(filter.isMarkingGood(new TypedNamedEntity(61, 21,
                "http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/Douglas_Robert_Dundas",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Individual",
                        "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Person")))));

        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Class",
                        "http://www.w3.org/2000/01/rdf-schema#Class")))));
        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2002/07/owl#Class")))));
        Assert.assertFalse(filter.isMarkingGood(new TypedNamedEntity(61, 21, "http://www.w3.org/2002/07/owl#Individual",
                new HashSet<String>(Arrays.asList("http://www.w3.org/2000/01/rdf-schema#Class")))));
    }
}
