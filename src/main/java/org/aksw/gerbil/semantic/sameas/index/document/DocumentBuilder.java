package org.aksw.gerbil.semantic.sameas.index.document;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentBuilder {

	private Set<String> sameAs = new HashSet<String>();
	private String dir;
	
	public void addSameAs(List<String> sameAs){
		StringBuilder builder =  new StringBuilder();
		for(String uri : sameAs){
			builder.append(uri+"\n");
		}
		this.sameAs.add(builder.toString());
	}
	
	public void createFiles() {
		new File(dir).mkdirs();
		for(String same : sameAs){
			File f = new File(dir+File.separator+same.hashCode()+"");
			try {
				f.createNewFile();
				PrintWriter pw = new PrintWriter(f);
				pw.print(same);
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
