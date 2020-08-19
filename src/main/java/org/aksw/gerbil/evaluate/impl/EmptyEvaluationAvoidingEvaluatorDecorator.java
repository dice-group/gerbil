package org.aksw.gerbil.evaluate.impl;

import java.util.Iterator;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This decorator can be used to decorate an Evaluator that should only be run
 * if there is at least one element in the given list of annotator results or in
 * the given list of expected gold standard results.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 * @param <T>
 */
public class EmptyEvaluationAvoidingEvaluatorDecorator<T extends Marking> extends AbstractEvaluatorDecorator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmptyEvaluationAvoidingEvaluatorDecorator.class);

    public EmptyEvaluationAvoidingEvaluatorDecorator(Evaluator<T> evaluator) {
        super(evaluator);
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results,String language) {
        int count = 0;
        Iterator<List<T>> iterator = goldStandard.iterator();
        List<T> singleResultList;
        while (iterator.hasNext() && (count == 0)) {
            singleResultList = iterator.next();
            if (singleResultList != null) {
                count += singleResultList.size();
            }
        }
        iterator = annotatorResults.iterator();
        while (iterator.hasNext() && (count == 0)) {
            singleResultList = iterator.next();
            if (singleResultList != null) {
                count += singleResultList.size();
            }
        }
        if (count > 0) {
            evaluator.evaluate(annotatorResults, goldStandard, results, language);
        } else {
            LOGGER.debug("There are no results that can be used for this evaluation. Returning.");
        }
    }

}
