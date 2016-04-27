package org.aksw.gerbil.qa.datatypes;

import java.util.Set;

import org.aksw.gerbil.transfer.nif.Marking;

import com.google.common.collect.Sets;

public class AnswerSet implements Marking {

    protected Set<String> answers = Sets.newHashSet();

    public AnswerSet(Set<String> answers) {
        this.answers = answers;
    }

    public Set<String> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<String> answers) {
        this.answers = answers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((answers == null) ? 0 : answers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnswerSet other = (AnswerSet) obj;
        if (answers == null) {
            if (other.answers != null)
                return false;
        } else if (!answers.equals(other.answers))
            return false;
        return true;
    }

    @Override
    public Object clone() {
        return new AnswerSet(answers);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AnswerSet [answers=");
        builder.append(answers.toString());
        builder.append("]");
        return builder.toString();
    }
}
