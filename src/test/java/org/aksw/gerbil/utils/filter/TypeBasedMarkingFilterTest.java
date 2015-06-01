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
package org.aksw.gerbil.utils.filter;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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
