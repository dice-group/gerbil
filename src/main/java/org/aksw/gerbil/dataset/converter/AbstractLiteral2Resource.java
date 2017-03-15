package org.aksw.gerbil.dataset.converter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLiteral2Resource implements Literal2Resource {


	@Override
	public Set<Set<String>> getResourcesForLiterals(Set<String> literals, String qLang) {
		Set<Set<String>> ret = new HashSet<Set<String>>();
		for(String literal : literals){
			ret.add(getResourcesForLiteral(literal, qLang));
		}
		return ret;
	}

	@Override
	public void close() throws IOException{
		
	}
}
