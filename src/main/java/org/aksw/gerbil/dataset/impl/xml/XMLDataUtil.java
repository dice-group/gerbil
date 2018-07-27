package org.aksw.gerbil.dataset.impl.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.utils.LengthBasedSpanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container class for the Util methods used in XML Dataset parsing
 * 
 * @author Nikit
 *
 */
public class XMLDataUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLDataUtil.class);

	/**
	 * Method to create a Document by making use of the params
	 * 
	 * @param dsName
	 *            - dataset name
	 * @param fileName
	 *            - file name
	 * @param text
	 *            - content of the file
	 * @param nes
	 *            - named entity markings
	 * @return - Generated Document
	 */
	public static Document createDocument(String dsName, String fileName, String text, List<XMLNamedEntity> nes) {
		String documentUri = generateDocumentUri(dsName, fileName);
		List<Marking> markings = new ArrayList<Marking>(nes.size());
		String retrievedSurfaceForm;
		for (XMLNamedEntity ne : nes) {
			retrievedSurfaceForm = text.substring(ne.getStartPosition(), ne.getStartPosition() + ne.getLength());
			if (!retrievedSurfaceForm.equals(ne.getSurfaceForm())) {
				LOGGER.warn("In document " + documentUri + ", the expected surface form of the named entity " + ne
						+ " does not fit the surface form derived from the text \"" + retrievedSurfaceForm + "\".");
			}
			addDBpediaUris(ne.getUris());
			markings.add(ne.toNamedEntity());
		}
		Document document = new DocumentImpl(text, documentUri, markings);
		mergeSubNamedEntity(document);
		return document;
	}

	/**
	 * Merge {@link NamedEntity}s that are sub spans of another named entity and
	 * that have the same URIs.
	 * 
	 * @param document
	 */
	public static void mergeSubNamedEntity(Document document) {
		List<NamedEntity> spanList = document.getMarkings(NamedEntity.class);
		NamedEntity nes[] = spanList.toArray(new NamedEntity[spanList.size()]);
		Arrays.sort(nes, new LengthBasedSpanComparator());
		Set<Marking> markingsToRemove = new HashSet<Marking>();
		boolean uriOverlapping;
		Iterator<String> uriIterator;
		for (int i = 0; i < nes.length; ++i) {
			uriOverlapping = false;
			for (int j = i + 1; (j < nes.length) && (!uriOverlapping); ++j) {
				// if nes[i] is a "sub span" of nes[j]
				if ((nes[i].getStartPosition() >= nes[j].getStartPosition()) && ((nes[i].getStartPosition()
						+ nes[i].getLength()) <= (nes[j].getStartPosition() + nes[j].getLength()))) {
					uriOverlapping = false;
					uriIterator = nes[i].getUris().iterator();
					while ((!uriOverlapping) && (uriIterator.hasNext())) {
						uriOverlapping = nes[j].containsUri(uriIterator.next());
					}
					if (uriOverlapping) {
						nes[j].getUris().addAll(nes[j].getUris());
						markingsToRemove.add(nes[i]);
					} else {
						LOGGER.debug("There are two overlapping named entities with different URI sets. {}, {}", nes[i],
								nes[j]);
					}
				}
			}
		}
		document.getMarkings().removeAll(markingsToRemove);
	}

	/**
	 * Method to generate a URI making use of dataset name and the file name
	 * 
	 * @param dsName
	 *            - name of the dataset
	 * @param fileName
	 *            - name of the file
	 * @return - Generated document URI
	 */
	public static String generateDocumentUri(String dsName, String fileName) {
		StringBuilder builder = new StringBuilder();
		builder.append("http://");
		builder.append(dsName);
		builder.append('/');
		builder.append(fileName);
		return builder.toString();
	}

	/**
	 * Adds DBpedia URIs by transforming Wikipeda URIs.
	 * 
	 * @param uris
	 */
	protected static void addDBpediaUris(Set<String> uris) {
		List<String> dbpediaUris = new ArrayList<String>(uris.size());
		for (String uri : uris) {
			if (uri.contains("en.wikipedia.org/wiki")) {
				dbpediaUris.add(uri.replace("en.wikipedia.org/wiki", "dbpedia.org/resource"));
			} else {
				dbpediaUris.add(uri.replace("wikipedia.org/wiki", "dbpedia.org/resource"));
			}
		}
		uris.addAll(dbpediaUris);
	}
}
