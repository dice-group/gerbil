package org.aksw.gerbil.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class SilverStandardCreator {

	private static final String DEFAULT_MAP_FILE_NAME = "silver_standard_map.tsv";
	private Map<String, Integer> freqMap = new HashMap<String, Integer>();
	
	private static final Property COSTUMER_PROPERTY = ResourceFactory.createProperty("http://ontology.thomsonreuters.com/supplyChain#customer");
	private static final Property SUPPLIER_PROPERTY = ResourceFactory.createProperty("http://ontology.thomsonreuters.com/supplyChain#supplier");
	private static final String OBJECT_NS = "https://permid.org/1-";
	
	public static void main(String[] args) throws IOException {
		SilverStandardCreator ssc = new SilverStandardCreator();
		Model silverStandard;
		if(args.length==1) {
			//assume its a map
			String[] arg = args[0].split("=");
			silverStandard = ssc.readFromMap(arg[0], Double.parseDouble(arg[1]));
		}
		else {
			Model[] models = new Model[args.length];
			for(int i =0;i<args.length;i++) {
				String arg = args[i];
				File f = new File(arg);
				models[i] = RDFDataMgr.loadModel(f.toURI().toString(), Lang.TURTLE);
				System.out.println("Loading file "+f);
			}
			silverStandard = ssc.createSilverStandard(models);
		}
		silverStandard.write(new FileWriter("silverStandard.nt"), "N-TRIPLE");
	}
	
	public Model createSilverStandard(Model... model) {
		
		for(Model m : model) {
			ResIterator resources = m.listSubjects();
			while(resources.hasNext()) {
				Resource res = resources.next();
				Integer old = 1;
				if(freqMap.containsKey(res.toString())) {
					old += freqMap.get(res.toString());
				}
				freqMap.put(res.toString(), old);
			}
		}
		saveMap(DEFAULT_MAP_FILE_NAME);
		return readFromMap(DEFAULT_MAP_FILE_NAME, model.length/2.0);
	}
	
	public boolean saveMap(String f) {
		try(PrintWriter pw = new PrintWriter(f)){
			for(String key : freqMap.keySet()) {
				pw.print(key);
				pw.print("\t");
				pw.println(freqMap.get(key));
			}
			
		}catch(IOException e) {
			System.out.println("Could not write map. Abort.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Model readFromMap(String f, double d) {
		Model silverStandard = ModelFactory.createDefaultModel();
		try(BufferedReader reader = new BufferedReader(new FileReader(f))){
			String line;
			while((line=reader.readLine())!=null) {
				if(line.isEmpty()) {
					continue;
				}
				String[] split = line.split("\t");
				Integer freq = Integer.parseInt(split[1]);
				if(d<freq) {
					//DO supplier_customer
					Resource subject = ResourceFactory.createResource(split[0]);
					String costumerSupplierID  = split[0].substring(split[0].lastIndexOf('/')+1);
					String[] csSplit = costumerSupplierID.split("_");
					Resource supplier = ResourceFactory.createResource(OBJECT_NS+csSplit[0]);
					Resource costumer = ResourceFactory.createResource(OBJECT_NS+csSplit[1]);
					silverStandard.add(subject, COSTUMER_PROPERTY, costumer);
					silverStandard.add(subject, SUPPLIER_PROPERTY, supplier);
				}
				//ignore
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return silverStandard;
	}
		
}
