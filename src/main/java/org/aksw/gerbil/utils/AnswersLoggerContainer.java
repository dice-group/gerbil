package org.aksw.gerbil.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.SubTaskEvaluator;
import org.aksw.gerbil.evaluate.TypeTransformingEvaluatorDecorator;
import org.aksw.gerbil.evaluate.impl.SimpleTypeTransformingEvaluatorDecorator;

public class AnswersLoggerContainer {

	private static Map<Integer, AnswersLogger<?>> alogs = new HashMap<Integer, AnswersLogger<?>>();

	public static void addAnswersLogger(Integer key, AnswersLogger<?> alog){
		alogs.put(key, alog);
	}
	
	public static List<AnswersLogger<?>> getAnswersLoggers(List<Evaluator<?>> evals){
		List<AnswersLogger<?>> ret = new ArrayList<AnswersLogger<?>>();
		for(Evaluator<?> e : evals){
			Integer key;
			if(e instanceof TypeTransformingEvaluatorDecorator){
				key = ((TypeTransformingEvaluatorDecorator) e).getDecorated().hashCode();
			}else{
				key = e.hashCode();
			}
			if(alogs.containsKey(key))
				ret.add(alogs.get(key));
		}
		return ret;
	}
	
	public static void remove(List<Evaluator<?>> evals){
		for(Evaluator<?> e : evals){
			Integer key;
			if(e instanceof TypeTransformingEvaluatorDecorator){
				key = ((TypeTransformingEvaluatorDecorator) e).getDecorated().hashCode();
			}else{
				key = e.hashCode();
			}
			if(alogs.containsKey(key))
				alogs.remove(key);
		}
	}

}
