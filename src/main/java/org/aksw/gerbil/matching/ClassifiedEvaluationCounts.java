package org.aksw.gerbil.matching;

public class ClassifiedEvaluationCounts extends EvaluationCounts {

    public EvaluationCounts classifiedCounts[];

    public ClassifiedEvaluationCounts(int numberOfClasses) {
        classifiedCounts = new EvaluationCounts[numberOfClasses];
        for (int i = 0; i < classifiedCounts.length; ++i) {
            classifiedCounts[i] = new EvaluationCounts();
        }
    }
}
