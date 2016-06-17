package org.aksw.gerbil.annotator.impl.xlisa;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.aksw.gerbil.annotator.A2KBAnnotator;
import org.aksw.gerbil.annotator.impl.AbstractAnnotator;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.ScoredNamedEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XLisaAnnotator extends AbstractAnnotator implements A2KBAnnotator {

	private static final String BASE_URI = "http://km.aifb.kit.edu/services/text-annotation/?";
	private static final String XLISA_LANG_1 = "org.aksw.gerbil.annotators.definition.XLisa.lang1"; 
	private static final String XLISA_LANG_2 = "org.aksw.gerbil.annotators.definition.XLisa.lang2";
	private static final String XLISA_KB = "org.aksw.gerbil.annotators.definition.XLisa.kb";
	private static final String XLISA_MODEL = "org.aksw.gerbil.annotators.definition.XLisa.model";
	
	private String lang1,lang2,kb,model;
	
	public XLisaAnnotator(){
		this.lang1 = GerbilConfiguration.getInstance().getString(XLISA_LANG_1);
		this.lang2 = GerbilConfiguration.getInstance().getString(XLISA_LANG_2);
		this.kb = GerbilConfiguration.getInstance().getString(XLISA_KB);
		this.model = GerbilConfiguration.getInstance().getString(XLISA_MODEL);
	}
	
	@Override
	public List<MeaningSpan> performD2KBTask(Document document)
			throws GerbilException {
		return sendRequest(document, true).getMarkings(MeaningSpan.class);
	}

	@Override
	public List<Span> performRecognition(Document document)
			throws GerbilException {
		return sendRequest(document, false).getMarkings(Span.class);
	}

	@Override
	public List<Meaning> performC2KB(Document document) throws GerbilException {
		return sendRequest(document, false).getMarkings(Meaning.class);
	}

	@Override
	public List<MeaningSpan> performA2KBTask(Document document)
			throws GerbilException {
		return sendRequest(document, false).getMarkings(MeaningSpan.class);
	}

	
	public Document sendRequest(Document document, boolean useSpans) throws GerbilException{
        Document resultDoc = new DocumentImpl(document.getText(), document.getDocumentURI());

		String model="model="+this.model;
		String source;
		try {
			source = "source="+URLEncoder.encode(document.getText(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new GerbilException("Couldn't send request to xLisa Annotator as the encoding of "+document.getText()+" does not work",
                    ErrorTypes.UNEXPECTED_EXCEPTION);
		}
		String lang1="lang1="+this.lang1, lang2="lang2="+this.lang2;
		String kb = "kb="+this.kb;
		String uri = BASE_URI+model+"&"+source+"&"+lang1+"&"+kb+"&"+lang2;
		
		HttpClient client = HttpClientBuilder.create().build();  
        HttpGet get = new HttpGet(uri);
        get.addHeader("Accept", "application/xml");
        get.addHeader("Content-Type", "application/xml");
        HttpResponse responsePost;
		try {
			responsePost = client.execute(get);
	        HttpEntity resEntity = responsePost.getEntity();
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//
            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            String responseXml = EntityUtils.toString(responsePost.getEntity());
            org.w3c.dom.Document doc = db.parse(resEntity.getContent());
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("DetectedTopic");
            
            for(int i = 0; i < nodeList.getLength(); i++){
                Element node = (Element) nodeList.item(i); 
                int startPosition = document.getText().indexOf(node.getAttribute("mention"));
//                if(Double.valueOf(node.getAttribute("weight"))<0.3){
//            		continue;
//            	}
                resultDoc.addMarking(new ScoredNamedEntity(startPosition, 
                		node.getAttribute("mention").length(), node.getAttribute("URL"), Double.valueOf(node.getAttribute("weight"))));
            }
            
		} catch (Exception e) {
			throw new GerbilException("Couldn't send request to xLisa Annotator",
                    ErrorTypes.ANNOTATOR_LOADING_ERROR);
		}  
		finally{
		}
		return resultDoc;
	}

	
	
}
