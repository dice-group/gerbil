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
package org.aksw.gerbil.semantic.subclass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.adapters.RDFReaderFactoryRIOT;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ClassHierarchyLoader {

    private static final Set<Property> ALLOWED_PROPERTIES = new HashSet<Property>(Arrays.asList(RDF.type,
            RDFS.subClassOf));

    private RDFReaderFactoryRIOT factory = new RDFReaderFactoryRIOT();

    public void loadClassHierarchy(File file, String rdfLang, String baseUri, Model model) throws IOException {
        Model readModel = ModelFactory.createDefaultModel();
        readClassHierarchy(file, rdfLang, baseUri, readModel);
        Set<Resource> classes = getClasses(readModel);
        copyClassHierarchy(readModel, model, classes);
    }

    protected void readClassHierarchy(File file, String rdfLang, String baseUri, Model model) throws IOException {
        FileInputStream fin = null;
        RDFReader rdfReader = factory.getReader(rdfLang);
        try {
            fin = new FileInputStream(file);
            rdfReader.read(model, fin, baseUri);
        } finally {
            IOUtils.closeQuietly(fin);
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
