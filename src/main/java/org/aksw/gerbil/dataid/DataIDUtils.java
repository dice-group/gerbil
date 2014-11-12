package org.aksw.gerbil.dataid;


public class DataIDUtils {
	
	/**
	 * Treats string removing slash, spaces, etc. 
	 * @param str
	 * @return
	 */
    public static String treatsNames(String str){		
    	// replace slash for underscore
    	str = str.replace("/", "_");
    	
    	// replace empty spaces for underscore
    	str = str.replace(" ", "_");
    	
    	// remove the suffix "-_test* -_train*"
    	String pattern = "[-_][Tt](([Ee][Ss].*)|([Rr][Aa][Ii][Nn].*).*)";
    	
    	str=str.replaceAll(pattern,"");
    	
    	return str;
    }
    
    
    
    
    
}
