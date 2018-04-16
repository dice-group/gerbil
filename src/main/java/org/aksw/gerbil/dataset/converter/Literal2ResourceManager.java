package org.aksw.gerbil.dataset.converter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Literal2ResourceManager extends AbstractLiteral2Resource {

	private Set<Literal2Resource> registeredConverter = new HashSet<Literal2Resource>();
	private String qLang;

	public Literal2ResourceManager() {
	}

	public void registerLiteral2Resource(Literal2Resource converter){
		this.registeredConverter.add(converter);
	}

	public void setQuestionLanguage(String qLang){
		this.qLang = qLang;
	}
	
	public Set<String> getResourcesForLiteral(String literal) {
		return getResourcesForLiteral(literal, qLang);
	}
	
	@Override
	public Set<String> getResourcesForLiteral(String literal, String qLang) {
		Set<String> ret = new HashSet<String>();
		if(registeredConverter.isEmpty()) {
			// just add literal
			ret.add(literal);
		}
		for(Literal2Resource converter : registeredConverter){
			ret.addAll(converter.getResourcesForLiteral(literal, qLang));
		}
		return ret;
	}
	
	public void close(){
		for(Literal2Resource converter : registeredConverter){
			try{
				converter.close();
			}
			catch(IOException e){
				//TODO Logger
			}
		}
	}
}
