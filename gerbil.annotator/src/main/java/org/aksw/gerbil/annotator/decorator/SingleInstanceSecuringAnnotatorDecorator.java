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
package org.aksw.gerbil.annotator.decorator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.SWCTask1System;
import org.aksw.gerbil.annotator.SWCTask2System;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a decorator for an {@link Annotator} which is used to make sure that
 * the decorated {@link Annotator} instance is used by one single thread at a
 * time. This is needed because of some Singleton based annotator
 * implementations that lead to an increase of the runtime measurement while
 * their threads are not working but waiting for a Semaphore. Thus, this
 * decorator should be used to decorate the time measurement.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public abstract class SingleInstanceSecuringAnnotatorDecorator extends AbstractAnnotatorDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceSecuringAnnotatorDecorator.class);

    public static SingleInstanceSecuringAnnotatorDecorator createDecorator(ExperimentType type, Annotator annotator) {
        switch (type) {
        case SWC2018T1:
        case SWC1:
            return new SingleInstanceSecuringSWC1System((SWCTask1System) annotator);
        case SWC_2019:
        case SWC2:
            return new SingleInstanceSecuringSWC2System((SWCTask2System) annotator);
        default:
            break;

        }
        LOGGER.error(
                "Couldn't generate a SingleInstanceSecuringAnnotatorDecorator for the given annotator. Returning null.");
        return null;
    }
    
    
	@Override
	public File2SystemEntry getFileMapping() {
		return decoratedAnnotator.getFileMapping();
	}


	@Override
	public void setFileMapping(File2SystemEntry entry) {
		decoratedAnnotator.setFileMapping(entry);
	}

    private static class SingleInstanceSecuringSWC1System extends SingleInstanceSecuringAnnotatorDecorator
            implements SWCTask1System {

        protected SingleInstanceSecuringSWC1System(SWCTask1System decoratedAnnotator) {
            super(decoratedAnnotator);
        }

        @Override
        public List<Model> performTask1(Model model) throws GerbilException {
            return SingleInstanceSecuringAnnotatorDecorator.performSWC1Task(this, model);
        }
    }

    private static class SingleInstanceSecuringSWC2System extends SingleInstanceSecuringAnnotatorDecorator
    	implements SWCTask2System {

    	protected SingleInstanceSecuringSWC2System(SWCTask2System decoratedAnnotator) {
    		super(decoratedAnnotator);
		}

		@Override
		public List<Model> performTask2(Model model) throws GerbilException {
			return SingleInstanceSecuringAnnotatorDecorator.performSWC2Task(this, model);
		}
    }

    protected static List<Model> performSWC1Task(SingleInstanceSecuringAnnotatorDecorator decorator, Model model)
            throws GerbilException {
        List<Model> result = null;
        try {
            decorator.semaphore.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
            throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        try {
            result = ((SWCTask1System) decorator.getDecoratedAnnotator()).performTask1(model);
        } finally {
            decorator.semaphore.release();
        }
        return result;
    }

    protected static List<Model> performSWC2Task(SingleInstanceSecuringAnnotatorDecorator decorator, Model model)
            throws GerbilException {
        List<Model> result = null;
        try {
            decorator.semaphore.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for the Annotator's semaphore.", e);
            throw new GerbilException("Interrupted while waiting for the Annotator's semaphore.", e,
                    ErrorTypes.UNEXPECTED_EXCEPTION);
        }
        try {
            result = ((SWCTask2System) decorator.getDecoratedAnnotator()).performTask2(model);
        } finally {
            decorator.semaphore.release();
        }
        return result;
    }

    
    /**
     * Registers the given {@link Annotator} (if it is not already present in
     * the registration) and returns its semaphore.
     * 
     * @param decoratedAnnotator
     * @return
     */
    protected static Semaphore registerAnnotator(Annotator decoratedAnnotator) {
        try {
            registryMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for registry mutex. Returning null.", e);
            return null;
        }
        Annotator annotator = decoratedAnnotator;
        while (annotator instanceof AnnotatorDecorator) {
            annotator = ((AnnotatorDecorator) annotator).getDecoratedAnnotator();
        }
        RegistryValue value;
        if (annotatorRegistry.containsKey(annotator)) {
            value = annotatorRegistry.get(annotator);
        } else {
            value = new RegistryValue();
            annotatorRegistry.put(annotator, value);
        }
        ++value.usageCounter;
        Semaphore semaphore = value.semaphore;
        registryMutex.release();
        return semaphore;
    }

    /**
     * Removes the given {@link Annotator} from the registration.
     * 
     * @param decoratedAnnotator
     */
    protected static void unregisterAnnotator(Annotator decoratedAnnotator) {
        try {
            registryMutex.acquire();
        } catch (InterruptedException e) {
            LOGGER.error("Exception while waiting for registry mutex. Aborting.");
            return;
        }
        Annotator annotator = decoratedAnnotator;
        while (annotator instanceof AnnotatorDecorator) {
            annotator = ((AnnotatorDecorator) annotator).getDecoratedAnnotator();
        }
        if (annotatorRegistry.containsKey(annotator)) {
            RegistryValue value = annotatorRegistry.get(annotator);
            --value.usageCounter;
            if (value.usageCounter == 0) {
                annotatorRegistry.remove(annotator);
            }
        } else {
            LOGGER.warn("Expected to find the annotator {} inside the registry but it wasn't there. Ignoring it.",
                    annotator.toString());
        }
        registryMutex.release();
    }

    protected static final Map<Annotator, RegistryValue> annotatorRegistry = new HashMap<Annotator, RegistryValue>();
    protected static final Semaphore registryMutex = new Semaphore(1);

    private final Semaphore semaphore;

    protected SingleInstanceSecuringAnnotatorDecorator(Annotator decoratedAnnotator) {
        super(decoratedAnnotator);
        semaphore = registerAnnotator(decoratedAnnotator);
    }

    @Override
    protected void finalize() throws Throwable {
        unregisterAnnotator(decoratedAnnotator);
        super.finalize();
    }

    private static class RegistryValue {
        public final Semaphore semaphore = new Semaphore(1);
        public int usageCounter = 0;
    }
}
