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
package org.aksw.gerbil.semantic.kb;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.aksw.gerbil.semantic.subclass.ClassNode;
import org.aksw.gerbil.semantic.subclass.ClassSet;
import org.aksw.gerbil.semantic.subclass.ClassifiedClassNode;
import org.aksw.gerbil.semantic.subclass.ClassifyingClassNodeFactory;
import org.aksw.gerbil.semantic.subclass.SimpleClassNode;
import org.aksw.gerbil.semantic.subclass.SimpleClassNodeFactory;
import org.aksw.gerbil.semantic.subclass.SimpleClassSet;
import org.aksw.gerbil.semantic.subclass.SimpleSubClassInferencerFactory;
import org.aksw.gerbil.semantic.subclass.SubClassInferencer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

@RunWith(Parameterized.class)
public class SimpleSubClassInferencerTest {

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        // The extractor returns nothing
        testConfigs.add(new Object[] {
                "http://example.org/A",
                new SimpleClassNode[] {
                        new SimpleClassNode("http://example.org/A"),
                        new SimpleClassNode("http://example.org/B"),
                        new SimpleClassNode("http://example2.org/D"),
                        new SimpleClassNode("http://example3.org/D"),
                        new SimpleClassNode(new HashSet<String>(Arrays.asList("http://example.org/C",
                                "http://example2.org/C", "http://example3.org/C"))) } });
        testConfigs.add(new Object[] {
                "http://example2.org/C",
                new SimpleClassNode[] {
                        new SimpleClassNode("http://example2.org/D"),
                        new SimpleClassNode("http://example3.org/D"),
                        new SimpleClassNode(new HashSet<String>(Arrays.asList("http://example.org/C",
                                "http://example2.org/C", "http://example3.org/C"))) } });
        testConfigs.add(new Object[] { "http://example2.org/D",
                new SimpleClassNode[] { new SimpleClassNode("http://example2.org/D") } });
        return testConfigs;
    }

    public static void main(String[] args) {
        // String rules =
        // "[sameAsRule: (?a owl:sameAs ?b) -> (?a owl:equivalentClass ?b)]\n[eqClassRule: (?a owl:equivalentClass ?b) -> (?b owl:equivalentClass ?a)]\n[subClass1: (?a rdfs:subClassOf ?b), (?a owl:sameAs ?c) -> (?c rdfs:subClassOf ?b)]\n[subClass1: (?a rdfs:subClassOf ?b), (?a owl:equivalentClass ?c) -> (?c rdfs:subClassOf ?b)]\n[subClass3: (?a rdfs:subClassOf ?b), (?b rdfs:subClassOf ?c) -> (?a rdfs:subClassOf ?c)]";
        String rules = "[sameAsReplaceRule: (?a owl:sameAs ?b) -> (?a owl:equivalentClass ?b), (?b owl:equivalentClass ?a)][eqClassRule1: (?a owl:equivalentClass ?b) -> (?b owl:equivalentClass ?a)][eqClassRule2: (?a owl:equivalentClass ?b), (?b owl:equivalentClass ?c) -> (?a owl:equivalentClass ?c), (?c owl:equivalentClass ?a)][subClass1: (?a rdfs:subClassOf ?b), (?a owl:equivalentClass ?c) -> (?c rdfs:subClassOf ?b)][subClass2: (?a rdfs:subClassOf ?b), (?b owl:equivalentClass ?c) -> (?a rdfs:subClassOf ?c)][subClass3: (?a rdfs:subClassOf ?b), (?b rdfs:subClassOf ?c) -> (?a rdfs:subClassOf ?c)]";
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        // Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        // reasoner.bindSchema(rawData);
        // Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner.setDerivationLogging(true);
        InfModel inf = ModelFactory.createInfModel(reasoner, createModel());
        Resource A = new ResourceImpl("http://example.org/A");

        PrintWriter out = new PrintWriter(System.out);
        for (StmtIterator i = inf.listStatements(null, RDFS.subClassOf, A); i.hasNext();) {
            Statement s = i.nextStatement();
            System.out.println("Statement is " + s);
            Iterator<Derivation> iterator = inf.getDerivation(s);
            if (iterator != null) {
                while (iterator.hasNext()) {
                    iterator.next().printTrace(out, true);
                }
            }
        }
        out.flush();
        Resource C2 = new ResourceImpl("http://example2.org/C");
        for (StmtIterator i = inf.listStatements(null, OWL.equivalentClass, C2); i.hasNext();) {
            Statement s = i.nextStatement();
            System.out.println("Statement is " + s);
            Iterator<Derivation> iterator = inf.getDerivation(s);
            if (iterator != null) {
                while (iterator.hasNext()) {
                    iterator.next().printTrace(out, true);
                }
            }
        }
        out.flush();
    }

    private String classUri;
    private SimpleClassNode expectedNodes[];
    private Random rand = new Random();

    public SimpleSubClassInferencerTest(String classUri, SimpleClassNode[] expectedNodes) {
        this.classUri = classUri;
        this.expectedNodes = expectedNodes;
    }

    @Test
    public void testInference() {
        SubClassInferencer inferencer = SimpleSubClassInferencerFactory.createInferencer(createModel());
        ClassSet classes = new SimpleClassSet();
        inferencer.inferSubClasses(classUri, classes, new SimpleClassNodeFactory());

        ClassNode node;
        for (int i = 0; i < expectedNodes.length; ++i) {
            node = classes.getNode(expectedNodes[i].getUris().iterator().next());
            Assert.assertEquals(expectedNodes[i], node);
        }
    }

    @Test
    public void testInferenceWithClassification() {
        SubClassInferencer inferencer = SimpleSubClassInferencerFactory.createInferencer(createModel());
        ClassSet classes = new SimpleClassSet();
        int classId = rand.nextInt();
        inferencer.inferSubClasses(classUri, classes, new ClassifyingClassNodeFactory(classId));

        ClassNode node;
        for (int i = 0; i < expectedNodes.length; ++i) {
            node = classes.getNode(expectedNodes[i].getUris().iterator().next());
            Assert.assertEquals(expectedNodes[i], node);
            Assert.assertTrue(((ClassifiedClassNode) node).getClassIds().contains(classId));
        }
    }

    private static Model createModel() {
        Model classModel = ModelFactory.createDefaultModel();
        Resource A = classModel.createResource("http://example.org/A");
        Resource B = classModel.createResource("http://example.org/B");
        Resource C = classModel.createResource("http://example.org/C");
        Resource C2 = classModel.createResource("http://example2.org/C");
        Resource C3 = classModel.createResource("http://example3.org/C");
        Resource D2 = classModel.createResource("http://example2.org/D");
        Resource D3 = classModel.createResource("http://example3.org/D");
        classModel.add(A, RDF.type, RDFS.Class);
        classModel.add(B, RDF.type, RDFS.Class);
        classModel.add(C, RDF.type, RDFS.Class);
        classModel.add(C2, RDF.type, RDFS.Class);
        classModel.add(C3, RDF.type, RDFS.Class);
        classModel.add(D2, RDF.type, RDFS.Class);
        classModel.add(D3, RDF.type, RDFS.Class);
        classModel.add(B, RDFS.subClassOf, A);
        classModel.add(C, RDFS.subClassOf, B);
        classModel.add(C, OWL.sameAs, C2);
        classModel.add(C3, OWL.equivalentClass, C);
        classModel.add(D2, RDFS.subClassOf, C2);
        classModel.add(D3, RDFS.subClassOf, C3);
        return classModel;
    }
}
