package org.aksw.gerbil.dataset.converter;

import java.io.IOException;
import java.util.Set;

public interface Literal2Resource {

	public Set<String> getResourcesForLiteral(String literal, String qLang);

	public Set<Set<String>> getResourcesForLiterals(Set<String> literal, String qLang);

	void close() throws IOException;

}
