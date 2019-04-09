package org.aksw.gerbil.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.aksw.simba.qaldbench.cli.Benchmarks;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

public class QaldGen {

	public static void main(String args[]) throws RepositoryException, MalformedQueryException, QueryEvaluationException, RDFHandlerException, IOException {
		String output = getParamVal(args, "-o");
		String tmpFileName = UUID.randomUUID().toString();
		replaceParamVal(args, "-o", tmpFileName);
		Benchmarks.main(args);
		RDF2JSON.main(new String[] {"qald", tmpFileName, output});
		File remove = new File(tmpFileName);
		remove.delete();
		
	}

	
	private static void replaceParamVal(String[] args, String param, String tmpFileName) {
		for(int index = 0 ; index<args.length;index++){
			 if(args[index].equals(param))
		 			 args[index] = tmpFileName;
		}
	}


	private static String getParamVal(String[] args, String param) {
		for(int index = 0 ; index<args.length;index++){
			 if(args[index].equals(param))
		 			 return args[index+1];
		}
		return null;
	}
}
