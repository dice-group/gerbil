package org.aksw.gerbil.qa.datatypes;

import java.util.Set;

public class ClassifiedAnswerSet extends AnswerSet {

    protected QuestionType questionType;

    public ClassifiedAnswerSet(Set<String> answers, QuestionType questionType) {
        super(answers);
        this.questionType = questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((questionType == null) ? 0 : questionType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassifiedAnswerSet other = (ClassifiedAnswerSet) obj;
        if (questionType != other.questionType)
            return false;
        return true;
    }

    @Override
    public Object clone() {
        return new ClassifiedAnswerSet(answers, questionType);
    }

}