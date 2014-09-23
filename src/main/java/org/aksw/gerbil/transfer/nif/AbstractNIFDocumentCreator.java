package org.aksw.gerbil.transfer.nif;

import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractNIFDocumentCreator implements NIFDocumentCreator {

	private String httpContentType;

	public AbstractNIFDocumentCreator(String httpContentType) {
		this.httpContentType = httpContentType;
	}

	@Override
	public String getDocumentAsNIFString(AnnotatedDocument document) {
		Model nifModel = createNIFModel(document);
		return generateNIFStringFromModel(nifModel);
	}

	protected abstract String generateNIFStringFromModel(Model nifModel);

	protected Model createNIFModel(AnnotatedDocument document) {
		Model nifModel = ModelFactory.createDefaultModel();
		// create the document node and add its properties
		Resource documentAsResource = nifModel.createResource(document
				.getDocumentURI());
		nifModel.add(documentAsResource, RDF.type, NIF.Context);
		nifModel.add(documentAsResource, RDF.type, NIF.String);
		nifModel.add(documentAsResource, RDF.type, NIF.RFC5147String);
		// FIXME add language to String
		nifModel.add(documentAsResource, NIF.isString, document.getText());
		// FIXME add predominant language
		// http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#predLang
		// FIXME determine and add length (this must be done together with the
		// parsing of the positions of the annotations)
		// FIXME add annotations
		return nifModel;
	}

	@Override
	public String getHttpContentType() {
		return httpContentType;
	}
}
