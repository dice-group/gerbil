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
package org.aksw.gerbil.dataset.check.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.dataset.check.EntityChecker;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.marking.MeaningsContainingMarking;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectObjectOpenHashMap;

/**
 * <p>
 * Standard implementation of the {@link EntityCheckerManager} interface.
 * Internally it uses a cache for storing the results of the
 * {@link EntityChecker}.
 * </p>
 * TODO The current implementation is not thread safe if
 * {@link #registerEntityChecker(String, EntityChecker)} is called while another
 * thread already is inside the {@link #checkMeanings(Collection)} method.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class EntityCheckerManagerImpl implements EntityCheckerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCheckerManagerImpl.class);

    private static final String SYNTHETIC_URI_NAME_SPACE = "http://aksw.org/unknown_entity/";

    private ObjectObjectOpenHashMap<String, EntityChecker> registeredCheckers = new ObjectObjectOpenHashMap<String, EntityChecker>();

    @Override
    public void registerEntityChecker(String namespace, EntityChecker checker) {
        registeredCheckers.put(namespace, checker);
    }

    @Override
    public void checkMarkings(Collection<? extends Marking> markings) {
        for (Marking marking : markings) {
            if (marking instanceof Meaning) {
                checkMeaning((Meaning) marking);
            } else if (marking instanceof MeaningsContainingMarking) {
                checkMeanings(((MeaningsContainingMarking) marking).getMeanings());
            }
        }
    }

    @Override
    public void checkMeanings(Collection<? extends Meaning> meanings) {
        for (Meaning meaning : meanings) {
            checkMeaning(meaning);
        }
    }

    public void checkMeaning(Meaning meaning) {
        Set<String> uris = meaning.getUris();
        List<String> wrongUris = null;
        List<String> newUris = null;
        for (String uri : uris) {
            // If the URI does not exist
            if ((uri != null) && (!checkUri(uri))) {
                if (wrongUris == null) {
                    wrongUris = new ArrayList<String>(3);
                    newUris = new ArrayList<String>(3);
                }
                wrongUris.add(uri);
                newUris.add(generateNewUri(uri));
            }
        }
        if (wrongUris != null) {
            LOGGER.info("Couldn't find an entity with the URIs={}.", wrongUris);
            uris.removeAll(wrongUris);
            uris.addAll(newUris);
        }
    }

    protected String generateNewUri(String uri) {
        StringBuilder newUri = new StringBuilder();
        newUri.append(SYNTHETIC_URI_NAME_SPACE);
        char chars[] = uri.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
            case '.': // falls through
            case ':':
            case '/': {
                newUri.append('_');
                break;
            }
            default: {
                newUri.append(chars[i]);
            }
            }
        }
        return newUri.toString();
    }

    public boolean checkUri(String uri) {
        String namespace;
        int matchingId = -1;
        for (int i = 0; (i < registeredCheckers.allocated.length) && (matchingId < 0); ++i) {
            if (registeredCheckers.allocated[i]) {
                namespace = (String) ((Object[]) registeredCheckers.keys)[i];
                if (uri.startsWith(namespace)) {
                    matchingId = i;
                }
            }
        }
        // If there is a checker available for this URI
        if (matchingId >= 0) {
            EntityChecker checker = (EntityChecker) ((Object[]) registeredCheckers.values)[matchingId];
            // Return whether this URI does exist
            return checker.entityExists(uri);
        } else {
            return true;
        }
    }

}
