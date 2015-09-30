/**
 * This file is part of NIF transfer library for the General Entity Annotator Benchmark.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NIF transfer library for the General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIF transfer library for the General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.io.nif;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class DocumentListParser {

    private static final String CONTEXT_PARAM_NAME = "context";
    private static final String TEXT_PARAM_NAME = "text";
    private static final String DOCUMENT_QUERY_STRING = "SELECT DISTINCT ?" + CONTEXT_PARAM_NAME + " ?"
            + TEXT_PARAM_NAME + " WHERE { ?context a <" + NIF.Context + "> . ?" + CONTEXT_PARAM_NAME + " <"
            + NIF.isString + "> ?" + TEXT_PARAM_NAME + " . }";

    private DocumentParser documentParser;

    public DocumentListParser() {
        this(false);
    }

    public DocumentListParser(boolean removeUsedProperties) {
        documentParser = new DocumentParser(removeUsedProperties);
    }

    public List<Document> parseDocuments(Model nifModel) {
        Query documentQuery = QueryFactory.create(DOCUMENT_QUERY_STRING);
        QueryExecution exec = QueryExecutionFactory.create(documentQuery, nifModel);
        ResultSet documentResult = exec.execSelect();

        // store the resources temporarily
        List<Resource> resources = new ArrayList<Resource>();
        QuerySolution solution;
        while (documentResult.hasNext()) {
            solution = documentResult.next();
            resources.add(solution.get(CONTEXT_PARAM_NAME).asResource());
        }

        List<Document> documents = new ArrayList<Document>();

        Document document;
        for (Resource documentResource : resources) {
            document = documentParser.getDocument(nifModel, documentResource);
            if (document != null) {
                documents.add(document);
            }
        }

        return documents;
    }
}
