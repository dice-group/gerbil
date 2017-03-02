package org.aksw.gerbil.web.config.check;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * A {@link Checker} that checks whether the given object(s) (interpreted as
 * String) are defined properties.
 * 
 *
 */
public class AnnotatorChecker implements Checker{
	private static Configuration config= GerbilConfiguration.getInstance();

	@Override
	public boolean check(Object... objects) {
		
		for(Object it: objects){
			Object prop=config.getProperty(it.toString());
			if(prop==null){
				return false;
			}
			
		}
		
		
		return true;
	}

}
