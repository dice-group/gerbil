/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TypedMarking;
import org.aksw.gerbil.transfer.nif.TypedSpan;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.aksw.gerbil.transfer.nif.data.TypedSpanImpl;

import com.carrotsearch.hppc.IntIntOpenHashMap;

/**
 * Merges {@link Span} instances if one is completely enclosed by the other.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 * @param <T>
 */
public class SpanMergingEvaluatorDecorator<T extends Span> extends AbstractEvaluatorDecorator<T> implements
        Comparator<Span> {

    public SpanMergingEvaluatorDecorator(Evaluator<T> evaluator) {
        super(evaluator);
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results,String language) {
        evaluator.evaluate(mergeListOfLists(annotatorResults), mergeListOfLists(goldStandard), results, language);
    }

    protected List<List<T>> mergeListOfLists(List<List<T>> spans) {
        List<List<T>> mergedLists = new ArrayList<List<T>>(spans.size());
        for (List<T> list : spans) {
            mergedLists.add(merge(list));
        }
        return mergedLists;
    }

    @SuppressWarnings("unchecked")
    protected List<T> merge(List<T> spans) {
        Span spanArray[] = spans.toArray(new Span[spans.size()]);
        Arrays.sort(spanArray, this);
        IntIntOpenHashMap enclosedByMap = new IntIntOpenHashMap();
        boolean isEnclosed;
        for (int i = 0; i < spanArray.length; ++i) {
            isEnclosed = false;
            for (int j = spanArray.length - 1; (j > i) && (!isEnclosed); --j) {
                // if spanArray[i] is enclosed by spanArray[j]
                if ((spanArray[i].getStartPosition() >= spanArray[j].getStartPosition())
                        && ((spanArray[i].getStartPosition() + spanArray[i].getLength()) <= (spanArray[j]
                                .getStartPosition() + spanArray[j].getLength()))) {
                    enclosedByMap.put(i, j);
                    isEnclosed = true;
                }
            }
        }
        // if no match could be found
        if (enclosedByMap.size() == 0) {
            return spans;
        }

        List<T> mergedMarkings = new ArrayList<T>(spans.size());
        // starting with the smallest span, check if a span is enclosed by
        // another
        int largerSpanId;
        for (int i = 0; i < spanArray.length; ++i) {
            if (enclosedByMap.containsKey(i)) {
                largerSpanId = enclosedByMap.lget();
                spanArray[largerSpanId] = merge(spanArray[i], spanArray[largerSpanId]);
            } else {
                mergedMarkings.add((T) spanArray[i]);
            }
        }
        return mergedMarkings;
    }

    private Span merge(Span smaller, Span larger) {
        Span newSpan;
        if ((smaller instanceof MeaningSpan) || (larger instanceof MeaningSpan)) {
            if ((smaller instanceof TypedSpan) || (larger instanceof TypedSpan)) {
                newSpan = new TypedNamedEntity(larger.getStartPosition(), larger.getLength(), new HashSet<String>(),
                        new HashSet<String>());
            } else {
                newSpan = new NamedEntity(larger.getStartPosition(), larger.getLength(), new HashSet<String>());
            }
        } else if ((smaller instanceof TypedSpan) || (larger instanceof TypedSpan)) {
            newSpan = new TypedSpanImpl(larger.getStartPosition(), larger.getLength(), new HashSet<String>());
        } else {
            newSpan = new SpanImpl(larger.getStartPosition(), larger.getLength());
        }
        updateNewSpan(newSpan, smaller);
        updateNewSpan(newSpan, larger);
        return newSpan;
    }

    private void updateNewSpan(Span newSpan, Span oldSpan) {
        // FIXME Scoring is missing!
        if (oldSpan instanceof Meaning) {
            for (String uri : ((Meaning) oldSpan).getUris()) {
                ((Meaning) newSpan).addUri(uri);
            }
        }
        if (oldSpan instanceof TypedMarking) {
            ((TypedMarking) newSpan).getTypes().addAll(((TypedMarking) oldSpan).getTypes());
        }
    }

    /**
     * Sorts the spans ascending by their length.
     */
    @Override
    public int compare(Span a1, Span a2) {
        int diff = a1.getLength() - a2.getLength();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
