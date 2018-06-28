package org.aksw.gerbil.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * Small URI and URL Validator helper
 * 
 * @author minimal
 *
 */
public class URIValidator {

	/**
	 * Tests a given string if it is a valid uri.
	 * 
	 * @param uri
	 * @return true if the string represents a valid uri, false otherwise
	 */
	public static boolean isValidURI(String uri) {
		try {
			new URI(uri);
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * Tests a given string if it is a valid url.
	 * 
	 * @param url
	 * @return true if the string represents a valid url, false otherwise
	 */
	public static boolean isValidURL(String url) {
		UrlValidator validator = UrlValidator.getInstance();
		return validator.isValid(url);

	}
}
