package org.aksw.gerbil.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class URIValidatorTest {

	@Test
	public void uriTests() {
		String uri = "http:/false.com";
		assertTrue(URIValidator.isValidURI(uri));
		uri = "http://false.com/Chicago, Ill.";
		assertFalse(URIValidator.isValidURI(uri));
		uri = "http://false.com/Chicago,_Ill.";
		assertTrue(URIValidator.isValidURI(uri));
	}

	
	@Test
	public void urlTests() {
		String url = "http:/false.com";
		assertFalse(URIValidator.isValidURL(url));
		url = "http://false.com/Chicago, Ill.";
		assertFalse(URIValidator.isValidURL(url));
		url = "http://false.com/Chicago,_Ill.";
		assertTrue(URIValidator.isValidURL(url));
	}
}
