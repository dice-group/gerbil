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
package org.aksw.gerbil.semantic.subclass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.adapters.RDFReaderFactoryRIOT;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class ClassHierarchyLoader {

    private static final Set<Property> ALLOWED_PROPERTIES = new HashSet<Property>(
            Arrays.asList(RDF.type, RDFS.subClassOf));

    private RDFReaderFactoryRIOT factory = new RDFReaderFactoryRIOT();

    public void loadClassHierarchy(File file, String rdfLang, String baseUri, Model model) throws IOException {
        Model readModel = ModelFactory.createDefaultModel();
        readClassHierarchy(file, rdfLang, baseUri, readModel);
        Set<Resource> classes = getClasses(readModel);
        copyClassHierarchy(readModel, model, classes);
    }

    protected void readClassHierarchy(File file, String rdfLang, String baseUri, Model model) throws IOException {
        InputStream is = null;
        RDFReader rdfReader = factory.getReader(rdfLang);
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            rdfReader.read(model, is, baseUri);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected void copyClassHierarchy(Model readModel, Model model, Set<Resource> classes) {
        StmtIterator stmtIterator = readModel.listStatements();
        Statement s;
        while (stmtIterator.hasNext()) {
            s = stmtIterator.next();
            if (classes.contains(s.getSubject()) && ALLOWED_PROPERTIES.contains(s.getPredicate())
                    && (!s.getObject().isAnon())) {
                model.add(s);
            }
        }
    }

    protected Set<Resource> getClasses(Model readModel) {
        ResIterator iterator = readModel.listSubjectsWithProperty(RDF.type, RDFS.Class);
        Resource r;
        Set<Resource> classes = new HashSet<Resource>();
        while (iterator.hasNext()) {
            r = iterator.next();
            if (!r.isAnon()) {
                classes.add(r);
            }
        }
        iterator = readModel.listSubjectsWithProperty(RDF.type, OWL.Class);
        while (iterator.hasNext()) {
            r = iterator.next();
            if (!r.isAnon()) {
                classes.add(r);
            }
        }
        return classes;
    }

    public static void main(String[] args) {
        ClassHierarchyLoader loader = new ClassHierarchyLoader();
        Model model = ModelFactory.createDefaultModel();
        try {
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/d0.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/d0.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/DUL.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/DUL.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/CollectionsLite.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/CollectionsLite.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/Conceptualization.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/Conceptualization.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/ontopic.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/ontopic.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/Roles.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/Roles.owl", model);
            loader.loadClassHierarchy(new File("gerbil_data/resources/hierarchies/Supplements.owl.xml"), "RDFXML",
                    "http://www.ontologydesignpatterns.org/ont/dul/Supplements.owl", model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream pout = null;
        try {
            pout = new PrintStream("test.txt");
            StmtIterator stmtIterator = model.listStatements();
            while (stmtIterator.hasNext()) {
                pout.println(stmtIterator.next().toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(pout);
        }
    }
}
