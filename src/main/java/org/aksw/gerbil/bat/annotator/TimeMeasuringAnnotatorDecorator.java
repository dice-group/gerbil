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
package org.aksw.gerbil.bat.annotator;

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Mention;
import it.unipi.di.acube.batframework.data.ScoredAnnotation;
import it.unipi.di.acube.batframework.data.ScoredTag;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.A2WSystem;
import it.unipi.di.acube.batframework.problems.C2WSystem;
import it.unipi.di.acube.batframework.problems.D2WSystem;
import it.unipi.di.acube.batframework.problems.Sa2WSystem;
import it.unipi.di.acube.batframework.problems.Sc2WSystem;
import it.unipi.di.acube.batframework.problems.TopicSystem;
import it.unipi.di.acube.batframework.systemPlugins.TimingCalibrator;
import it.unipi.di.acube.batframework.utils.AnnotationException;

import java.util.Calendar;
import java.util.HashSet;

/**
 * This is a simple decorator for an annotator which measures the time needed for annotations. This task is handled by
 * this annotator decorator due to an easier adapter implementation and time measuring problems if errors occur inside
 * the adapter.
 * 
 * @author Michael Röder
 * 
 */
public class TimeMeasuringAnnotatorDecorator {

    public static TopicSystem createDecorator(TopicSystem annotator) {
        if (annotator instanceof Sa2WSystem) {
            return new TimeMeasuringSa2W((Sa2WSystem) annotator);
        }
        if (annotator instanceof Sc2WSystem) {
            return new TimeMeasuringSc2W((Sc2WSystem) annotator);
        }
        if (annotator instanceof A2WSystem) {
            return new TimeMeasuringA2W((A2WSystem) annotator);
        }
        if (annotator instanceof D2WSystem) {
            return new TimeMeasuringD2W((D2WSystem) annotator);
        }
        if (annotator instanceof C2WSystem) {
            return new TimeMeasuringC2W((C2WSystem) annotator);
        }
        return null;
    }

    private static class AbstractTimeMeter implements TopicSystem {

        private long lastTime = -1;
        private long calib = -1;
        protected TopicSystem decoratedAnnotator;

        public AbstractTimeMeter(TopicSystem decoratedAnnotator) {
            this.decoratedAnnotator = decoratedAnnotator;
        }

        @Override
        public String getName() {
            return decoratedAnnotator.getName();
        }

        @Override
        public long getLastAnnotationTime() {
            if (calib == -1)
                calib = TimingCalibrator.getOffset(this);
            return lastTime - calib > 0 ? lastTime - calib : 0;
        }

        protected TopicSystem getDecoratedAnnotator() {
            return decoratedAnnotator;
        }

        protected void startTimeMeasuring() {
            lastTime = Calendar.getInstance().getTimeInMillis();
        }

        protected void stopTimeMeasuring() {
            lastTime = Calendar.getInstance().getTimeInMillis() - lastTime;
        }
    }

    private static class TimeMeasuringD2W extends AbstractTimeMeter
            implements D2WSystem {

        public TimeMeasuringD2W(D2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Annotation> solveD2W(String text,
                HashSet<Mention> mentions) throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveD2W(this, text,
                    mentions);
        }
    }

    private static class TimeMeasuringA2W extends TimeMeasuringD2W implements
            A2WSystem {

        public TimeMeasuringA2W(A2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveC2W(this, text);
        }

        @Override
        public HashSet<Annotation> solveA2W(String text)
                throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveA2W(this, text);
        }
    }

    private static class TimeMeasuringSa2W extends TimeMeasuringA2W implements
            Sa2WSystem {

        public TimeMeasuringSa2W(Sa2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<ScoredTag> solveSc2W(String text)
                throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveSc2W(this, text);
        }

        @Override
        public HashSet<ScoredAnnotation> solveSa2W(String text)
                throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveSa2W(this, text);
        }
    }

    private static class TimeMeasuringC2W extends AbstractTimeMeter
            implements C2WSystem {

        public TimeMeasuringC2W(C2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveC2W(this, text);
        }
    }

    private static class TimeMeasuringSc2W extends TimeMeasuringC2W implements
            Sc2WSystem {

        public TimeMeasuringSc2W(Sc2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<ScoredTag> solveSc2W(String text)
                throws AnnotationException {
            return TimeMeasuringAnnotatorDecorator.solveSc2W(this, text);
        }
    }

    protected static HashSet<Tag> solveC2W(AbstractTimeMeter timeMeter,
            String text) throws AnnotationException {
        timeMeter.startTimeMeasuring();
        HashSet<Tag> result = null;
        try {
            result = ((C2WSystem) timeMeter.getDecoratedAnnotator())
                    .solveC2W(text);
        } finally {
            timeMeter.stopTimeMeasuring();
        }
        return result;
    }

    protected static HashSet<Annotation> solveD2W(
            AbstractTimeMeter timeMeter, String text,
            HashSet<Mention> mentions) {
        timeMeter.startTimeMeasuring();
        HashSet<Annotation> result = null;
        try {
            result = ((D2WSystem) timeMeter.getDecoratedAnnotator()).solveD2W(
                    text, mentions);
        } finally {
            timeMeter.stopTimeMeasuring();
        }
        return result;
    }

    protected static HashSet<Annotation> solveA2W(
            AbstractTimeMeter timeMeter, String text) {
        timeMeter.startTimeMeasuring();
        HashSet<Annotation> result = null;
        try {
            result = ((A2WSystem) timeMeter.getDecoratedAnnotator())
                    .solveA2W(text);
        } finally {
            timeMeter.stopTimeMeasuring();
        }
        return result;
    }

    protected static HashSet<ScoredTag> solveSc2W(
            AbstractTimeMeter timeMeter, String text) {
        timeMeter.startTimeMeasuring();
        HashSet<ScoredTag> result = null;
        try {
            result = ((Sc2WSystem) timeMeter.getDecoratedAnnotator())
                    .solveSc2W(text);
        } finally {
            timeMeter.stopTimeMeasuring();
        }
        return result;
    }

    protected static HashSet<ScoredAnnotation> solveSa2W(
            AbstractTimeMeter timeMeter, String text)
            throws AnnotationException {
        timeMeter.startTimeMeasuring();
        HashSet<ScoredAnnotation> result = null;
        try {
            result = ((Sa2WSystem) timeMeter.getDecoratedAnnotator())
                    .solveSa2W(text);
        } finally {
            timeMeter.stopTimeMeasuring();
        }
        return result;
    }
}
