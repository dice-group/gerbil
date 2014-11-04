package org.aksw.gerbil.bat.annotator;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Mention;
import it.acubelab.batframework.data.ScoredAnnotation;
import it.acubelab.batframework.data.ScoredTag;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.problems.C2WSystem;
import it.acubelab.batframework.problems.D2WSystem;
import it.acubelab.batframework.problems.Sa2WSystem;
import it.acubelab.batframework.problems.Sc2WSystem;
import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.utils.AnnotationException;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple decorator for an annotator which handles exceptions thrown
 * by the decorated annotator. It logs these exceptions and counts the errors.
 * This behavior makes it possible, that the BAT-Framework doesn't quit the
 * experiment even if an exception is thrown.
 * 
 * @author Michael Röder
 * 
 */
public class ErrorCountingAnnotatorDecorator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ErrorCountingAnnotatorDecorator.class);

    public static TopicSystem createDecorator(TopicSystem annotator) {
        if (annotator instanceof Sa2WSystem) {
            return new ErrorCountingSa2W((Sa2WSystem) annotator);
        }
        if (annotator instanceof Sc2WSystem) {
            return new ErrorCountingSc2W((Sc2WSystem) annotator);
        }
        if (annotator instanceof A2WSystem) {
            return new ErrorCountingA2W((A2WSystem) annotator);
        }
        if (annotator instanceof D2WSystem) {
            return new ErrorCountingD2W((D2WSystem) annotator);
        }
        if (annotator instanceof C2WSystem) {
            return new ErrorCountingC2W((C2WSystem) annotator);
        }
        return null;
    }

    private static class AbstractErrorCounter implements ErrorCounter,
            TopicSystem {
        protected int errorCount = 0;

        protected TopicSystem decoratedAnnotator;

        public AbstractErrorCounter(TopicSystem decoratedAnnotator) {
            this.decoratedAnnotator = decoratedAnnotator;
        }

        @Override
        public int getErrorCount() {
            return errorCount;
        }

        @Override
        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }

        @Override
        public String getName() {
            return decoratedAnnotator.getName();
        }

        @Override
        public long getLastAnnotationTime() {
            return decoratedAnnotator.getLastAnnotationTime();
        }

        protected TopicSystem getDecoratedAnnotator() {
            return decoratedAnnotator;
        }

        protected void increaseErrorCount() {
            ++errorCount;
        }
    }

    private static class ErrorCountingD2W extends AbstractErrorCounter
            implements D2WSystem {

        public ErrorCountingD2W(D2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Annotation> solveD2W(String text,
                HashSet<Mention> mentions) throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveD2W(this, text,
                    mentions);
        }
    }

    private static class ErrorCountingA2W extends ErrorCountingD2W implements
            A2WSystem {

        public ErrorCountingA2W(A2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveC2W(this, text);
        }

        @Override
        public HashSet<Annotation> solveA2W(String text)
                throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveA2W(this, text);
        }
    }

    private static class ErrorCountingSa2W extends ErrorCountingA2W implements
            Sa2WSystem {

        public ErrorCountingSa2W(Sa2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<ScoredTag> solveSc2W(String text)
                throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveSc2W(this, text);
        }

        @Override
        public HashSet<ScoredAnnotation> solveSa2W(String text)
                throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveSa2W(this, text);
        }
    }

    private static class ErrorCountingC2W extends AbstractErrorCounter
            implements C2WSystem {

        public ErrorCountingC2W(C2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<Tag> solveC2W(String text) throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveC2W(this, text);
        }
    }

    private static class ErrorCountingSc2W extends ErrorCountingC2W implements
            Sc2WSystem {

        public ErrorCountingSc2W(Sc2WSystem decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public HashSet<ScoredTag> solveSc2W(String text)
                throws AnnotationException {
            return ErrorCountingAnnotatorDecorator.solveSc2W(this, text);
        }
    }

    protected static HashSet<Tag> solveC2W(AbstractErrorCounter errorCounter,
            String text) throws AnnotationException {
        HashSet<Tag> result = null;
        try {
            result = ((C2WSystem) errorCounter.getDecoratedAnnotator())
                    .solveC2W(text);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new HashSet<Tag>(0);
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Tag a : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Tag(wId=");
                builder.append(a.getConcept());
                builder.append(')');
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static HashSet<Annotation> solveD2W(
            AbstractErrorCounter errorCounter, String text,
            HashSet<Mention> mentions) {
        HashSet<Annotation> result = null;
        try {
            result = ((D2WSystem) errorCounter.getDecoratedAnnotator()).solveD2W(
                    text, mentions);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new HashSet<Annotation>(0);
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Annotation a : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Annotation(pos=");
                builder.append(a.getPosition());
                builder.append(",l=");
                builder.append(a.getLength());
                builder.append(",wId=");
                builder.append(a.getConcept());
                builder.append(')');
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static HashSet<Annotation> solveA2W(
            AbstractErrorCounter errorCounter, String text) {
        HashSet<Annotation> result = null;
        try {
            result = ((A2WSystem) errorCounter.getDecoratedAnnotator())
                    .solveA2W(text);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new HashSet<Annotation>(0);
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (Annotation a : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("Annotation(pos=");
                builder.append(a.getPosition());
                builder.append(",l=");
                builder.append(a.getLength());
                builder.append(",wId=");
                builder.append(a.getConcept());
                builder.append(')');
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static HashSet<ScoredTag> solveSc2W(
            AbstractErrorCounter errorCounter, String text) {
        HashSet<ScoredTag> result = null;
        try {
            result = ((Sc2WSystem) errorCounter.getDecoratedAnnotator())
                    .solveSc2W(text);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new HashSet<ScoredTag>(0);
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (ScoredTag t : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("ScoredTag(wId=");
                builder.append(t.getConcept());
                builder.append(",s=");
                builder.append(t.getScore());
                builder.append(')');
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }

    protected static HashSet<ScoredAnnotation> solveSa2W(
            AbstractErrorCounter errorCounter, String text)
            throws AnnotationException {
        HashSet<ScoredAnnotation> result = null;
        try {
            result = ((Sa2WSystem) errorCounter.getDecoratedAnnotator())
                    .solveSa2W(text);
        } catch (Exception e) {
            if (errorCounter.getErrorCount() == 0) {
                // Log only the first exception completely
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + ")", e);
            } else {
                // Log only the Exception message without the stack trace
                LOGGER.error("Got an Exception from the annotator ("
                        + errorCounter.getName() + "): "
                        + e.getLocalizedMessage());
            }
            errorCounter.increaseErrorCount();
            return new HashSet<ScoredAnnotation>(0);
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(errorCounter.getName());
            builder.append("] result=[");
            boolean first = true;
            for (ScoredAnnotation a : result) {
                if (first) {
                    first = false;
                } else {
                    builder.append(',');
                }
                builder.append("ScoredAnnotation(pos=");
                builder.append(a.getPosition());
                builder.append(",l=");
                builder.append(a.getLength());
                builder.append(",wId=");
                builder.append(a.getConcept());
                builder.append(",s=");
                builder.append(a.getScore());
                builder.append(')');
            }
            builder.append(']');
            LOGGER.debug(builder.toString());
        }
        return result;
    }
}
