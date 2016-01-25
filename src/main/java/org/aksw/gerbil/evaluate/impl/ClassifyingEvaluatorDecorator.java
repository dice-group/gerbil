package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.marking.ClassifiedMarking;
import org.aksw.gerbil.datatypes.marking.ClassifiedMarkingFactory;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.impl.clas.MarkingClassifier;
import org.aksw.gerbil.transfer.nif.Marking;

public class ClassifyingEvaluatorDecorator<T extends Marking, U extends ClassifiedMarking> implements Evaluator<T> {

    protected Evaluator<U> evaluator;
    protected MarkingClassifier<U> classifiers[];

    public ClassifyingEvaluatorDecorator(Evaluator<U> evaluator,
            @SuppressWarnings("unchecked") MarkingClassifier<U>... classifiers) {
        this.evaluator = evaluator;
        this.classifiers = classifiers;
    }

    public Evaluator<U> getDecorated() {
        return evaluator;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        evaluator.evaluate(classify(annotatorResults), classify(goldStandard), results);
    }

    protected List<List<U>> classify(List<List<T>> markings) {
        List<List<U>> classifiedMarkings = new ArrayList<List<U>>(markings.size());
        for (List<T> markingsList : markings) {
            classifiedMarkings.add(classifyList(markingsList));
        }
        return classifiedMarkings;
    }

    @SuppressWarnings("unchecked")
    private List<U> classifyList(List<T> markings) {
        List<U> classifiedMarkings = new ArrayList<U>(markings.size());
        U classifiedMarking;
        for (T marking : markings) {
            classifiedMarking = (U) ClassifiedMarkingFactory.createClassifiedMeaning(marking);
            for (int i = 0; i < classifiers.length; ++i) {
                classifiers[i].classify(classifiedMarking);
            }
            classifiedMarkings.add(classifiedMarking);
        }
        return classifiedMarkings;
    }

}
