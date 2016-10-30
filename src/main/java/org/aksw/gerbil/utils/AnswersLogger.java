package org.aksw.gerbil.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.qa.datatypes.AnswerSet;
import org.aksw.gerbil.transfer.nif.Marking;

public class AnswersLogger<T extends Marking> {

	private static final String FOLDER = "answer_tables";
	
	private PrintWriter pw;
	private int experimentID;
	private List<String> questions = new ArrayList<String>();
	private String matchingID;
	private String system;

	
	public AnswersLogger(String string){
		this.matchingID = string;
	}
	
	public AnswersLogger(int experimentTaskId){
		this.experimentID=experimentTaskId;
	}
	
	
	public AnswersLogger(int experimentTaskId, List<String> questions){
		this.experimentID=experimentTaskId;
		this.questions=questions;
	}
	
	public void setExpID(int experimentTaskId){
		this.experimentID=experimentTaskId;
	}
	public void setSystem(String system){
		this.system = system;
	}
	
	public void addQuestion(String question){
		this.questions.add(question);
	}
	
	public void open() throws IOException{
		File dir = new File(FOLDER);
		if(!dir.exists() || !dir.isDirectory()){
			dir.mkdir();
		}
		File f = new File(FOLDER+File.separator+"answers_"+experimentID+"_"+system+"_"+matchingID+".csv");
		f.createNewFile();
		pw = new PrintWriter(f);
		pw.println("Question\t Annotator Answers\t Golden Answers\t tp\t fp\t fn\t pre\t rec\t f1");
	}
	
	public void close(){
		pw.close();
	}
	
	public void printLine(int index, List<T> annotator, List<T> golden, EvaluationCounts counts, double[] measure){
		StringBuilder print = new StringBuilder();
		print.append(questions.get(index).replace("\t", "  "));
		print.append("\t");
    	try{
    		if(annotator.get(0) instanceof AnswerSet){
    			List<AnswerSet<T>> a = (List<AnswerSet<T>>) annotator;
    			for(AnswerSet<T> as : a){
    				print.append(as.getAnswers().toString().replace("\t", "  "));
    			}
    		}else{
    			print.append(annotator.toString().replace("\t", "  "));
    		}
    	}catch(Exception e){
    		print.append(annotator.toString().replace("\t", "  "));
    	}
    	
    	print.append("\t");
    	try{
    	if(golden.get(0) instanceof AnswerSet){
    		List<AnswerSet<T>> a = (List<AnswerSet<T>>) golden;
    		for(AnswerSet<T> as : a){
    			print.append(as.getAnswers().toString().replace("\t", "  "));
    		}
    	}else{
    		print.append(golden.toString().replace("\t", "  "));
    	}
    	}catch(Exception e){
    		print.append(golden.toString().replace("\t", "  "));
    	}
    	print.append("\t");
    	print.append(counts.truePositives);
    	print.append("\t");
    	print.append(counts.falsePositives);
    	print.append("\t");
    	print.append(counts.falseNegatives);
    	print.append("\t");
    	print.append(measure[0]);
    	print.append("\t");
    	print.append(measure[1]);
    	print.append("\t");
    	print.append(measure[2]);
    	pw.println(print.toString());
	}
	
	public void printMicro(EvaluationCounts sums, double[] measures){
		StringBuilder print = new StringBuilder();
    	print.append("sum-tp: ");
    	print.append(sums.truePositives);
    	print.append("\t sum-fp: ");
    	print.append(sums.falsePositives);
    	print.append("\t sum-fn: ");
    	print.append(sums.falseNegatives);
    	print.append("\t micro-pre: ");
    	print.append(measures[0]);
    	print.append("\t micro-rec: ");
    	print.append(measures[1]);
    	print.append("\t micro-f1: ");
    	print.append(measures[2]);
    	pw.println(print.toString());
	}
	
	public void printMacro(double[] avgs){
		StringBuilder print = new StringBuilder();
    	print.append("macro-pre: ");
    	print.append(avgs[0]);
    	print.append("\t macro-rec: ");
    	print.append(avgs[1]);
    	print.append("\t macro-f1: ");
    	print.append(avgs[2]);
    	pw.println(print.toString());
	}
	
}
