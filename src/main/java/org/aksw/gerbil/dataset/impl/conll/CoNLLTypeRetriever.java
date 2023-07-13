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
package org.aksw.gerbil.dataset.impl.conll;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to retrieve entity types for the specified annotations from a CoNLL
 * formatted dataset.
 */
public class CoNLLTypeRetriever {

    private static final String PLACE_URI = "http://dbpedia.org/ontology/Place";
    private static final String COMPANY_URI = "http://dbpedia.org/ontology/Company";
    private static final String FILM_URI = "http://dbpedia.org/ontology/Film";
    private static final String MUSICAL_ARTIST_URI = "http://dbpedia.org/ontology/MusicalArtist";
    private static final String UNKNOWN_URI = "http://dbpedia.org/ontology/Unknown";
    private static final String PERSON_URI = "http://dbpedia.org/ontology/Person";
    private static final String PRODUCT_URI = "http://dbpedia.org/ontology/product"; // TODO this IRI is a property but
                                                                                     // not a class.
    private static final String SPORTS_TEAM_URI = "http://dbpedia.org/ontology/SportsTeam";
    private static final String TV_SHOW_URI = "http://dbpedia.org/ontology/TelevisionShow";
    private static final String ORGANISATION_URI = "http://dbpedia.org/ontology/Organisation";
    private static final String LANGUAGE = "https://dbpedia.org/ontology/language";

    private Map<String, String> annotationToType;

    public CoNLLTypeRetriever(String place, String company, String film, String musicalArtist, String unknown,
            String person, String product, String sportsTeam, String tvShow, String organisation, String language) {
        annotationToType = new HashMap<>();
        annotationToType.put(place, PLACE_URI);
        annotationToType.put(company, COMPANY_URI);
        annotationToType.put(film, FILM_URI);
        annotationToType.put(musicalArtist, MUSICAL_ARTIST_URI);
        annotationToType.put(unknown, UNKNOWN_URI);
        annotationToType.put(person, PERSON_URI);
        annotationToType.put(product, PRODUCT_URI);
        annotationToType.put(sportsTeam, SPORTS_TEAM_URI);
        annotationToType.put(tvShow, TV_SHOW_URI);
        annotationToType.put(organisation, ORGANISATION_URI);
        annotationToType.put(language, LANGUAGE);
    }

    /**
     * Returns the IRI for the given key or {@code null} if there is no value for
     * the given key.
     * 
     * @param key the type key found in the dataset
     * @return the IRI of the type
     */
    public String getTypeURI(String key) {
        return annotationToType.getOrDefault(key, null);
    }

    /**
     * Adds the given type key IRI mapping.
     * 
     * @param key the type key that can be found in the dataset
     * @param uri the IRI that should be returned for this keys
     */
    public void addTypeURI(String key, String uri) {
        annotationToType.put(key, uri);
    }
}