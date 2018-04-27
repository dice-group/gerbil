package org.aksw.gerbil.annotator.impl.sw;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFWebServiceSystem extends AbstractHttpBasedAnnotator implements SWCTask1System, SWCTask2System {

	 private static final Logger LOGGER = LoggerFactory.getLogger(RDFWebServiceSystem.class);
	
	private File2SystemEntry fileMapping;
	private String url;

	public RDFWebServiceSystem(String annotatorName, String url) {
		super(annotatorName);
		this.url=url;
	}

	protected static void closeInputStream(InputStream inputStream) {
		try {
			inputStream.close();
		} catch (Exception e) {
		}
	}

	@Override
	public List<Model> performTask2(Model model) throws GerbilException {
		return performKBC(model);
	}

	@Override
	public List<Model> performTask1(Model model) throws GerbilException {
		return performKBC(model);
	}

	private List<Model> performKBC(Model model) throws GerbilException {
		List<Model> answerList = new LinkedList<Model>();
		StringWriter modelAsString = new StringWriter();
		model.write(modelAsString, "TTL");
		HttpEntity entity = new StringEntity(modelAsString.toString(), "UTF-8");
		// send NIF document
		HttpPost request = null;
		try {
			request = createPostRequest(url);
		} catch (IllegalArgumentException e) {
			throw new GerbilException("Couldn't create HTTP request.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		request.setEntity(entity);
		request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle;charset=UTF-8");
		request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
		request.addHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

		entity = null;
		CloseableHttpResponse response = null;
		try {
			response = sendRequest(request, true);
			// receive TTL File
			entity = response.getEntity();
			// read response and parse NIF
			try {

				Model recvModel = ModelFactory.createDefaultModel();
				RDFDataMgr.read(recvModel, entity.getContent(), Lang.TTL);
				answerList.add(recvModel);
			} catch (Exception e) {
				LOGGER.error("Couldn't parse the response.", e);
				throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);
			}
		} finally {
			closeRequest(request);
			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException e1) {
				}
			}
			IOUtils.closeQuietly(response);
		}
		return answerList;
	}

	@Override
	public File2SystemEntry getFileMapping() {
		return this.fileMapping;
	}

	@Override
	public void setFileMapping(File2SystemEntry entry) {
		this.fileMapping = entry;
	}
}
