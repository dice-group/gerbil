package org.aksw.gerbil.annotator.impl.qa;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.annotator.QASystem;
import org.aksw.gerbil.annotator.http.AbstractHttpBasedAnnotator;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.qa.QAUtils;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedQALDBasedWebService extends AbstractHttpBasedAnnotator implements QASystem {

	private String name;
	private final String url;
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedQALDBasedWebService.class);

	public static void main(final String[] argc) throws GerbilException, IOException {
		ExtendedQALDBasedWebService service = new ExtendedQALDBasedWebService("https://wdaqua-qanary.univ-st-etienne.fr/gerbil-execute/wdaqua-core0,%20QueryExecuter/");
		Document document = new DocumentImpl();
		document.setText("When was Barack Obama born?");
		service.answerQuestion(document, "en");
		service.close();
	}

	public ExtendedQALDBasedWebService(final String url) {
		super();
		this.url = url;
	}

	public ExtendedQALDBasedWebService(final String url, final String name) {
		super(name);
		this.url = url;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public List<Marking> answerQuestion(final Document document, final String questionLang) throws GerbilException {

		RequestConfig requestConfig = RequestConfig.custom().build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		HttpPost httppost = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("query", document.getText()));

		params.add(new BasicNameValuePair("lang", questionLang));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
		httppost.setEntity(entity);

		List<Marking> ret = null;
		CloseableHttpResponse response = null;

		try {

			response = client.execute(httppost);
			// receive NIF document
			InputStream is = response.getEntity().getContent();
			// read response and parse NIF
			Object json = ExtendedQALDJSONLoader.readJson(is);

			List<IQuestion> questions = EJQuestionFactory.getQuestionsFromJson(json);

			Document resultDoc = QAUtils.translateQuestion(questions.get(0), null, questionLang);
			ret = resultDoc.getMarkings();
		} catch (Exception e) {
			LOGGER.error("Couldn't parse the response.", e);
			throw new GerbilException("Couldn't parse the response.", e, ErrorTypes.UNEXPECTED_EXCEPTION);

		} finally {

			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException e1) {
				}
			}
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(client);
		}

		return ret;
	}

}
