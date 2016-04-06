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
package org.aksw.gerbil.semantic.sameas.impl.wiki;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.semantic.sameas.SingleUriSameAsRetriever;
import org.aksw.gerbil.semantic.sameas.impl.DomainBasedSameAsRetrieverManager;
import org.aksw.gerbil.semantic.sameas.impl.SimpleDomainExtractor;

public class WikiDbPediaBridgingSameAsRetriever implements SingleUriSameAsRetriever {

    private static final String URI_PROTOCOL_PART = "http://";

    private static final String BG_DBPEDIA_DOMAIN = "bg.dbpedia.org";
    private static final String CA_DBPEDIA_DOMAIN = "ca.dbpedia.org";
    private static final String CS_DBPEDIA_DOMAIN = "cs.dbpedia.org";
    private static final String DE_DBPEDIA_DOMAIN = "de.dbpedia.org";
    private static final String EN_DBPEDIA_DOMAIN = "dbpedia.org";
    private static final String ES_DBPEDIA_DOMAIN = "es.dbpedia.org";
    private static final String EU_DBPEDIA_DOMAIN = "eu.dbpedia.org";
    private static final String FR_DBPEDIA_DOMAIN = "fr.dbpedia.org";
    private static final String HU_DBPEDIA_DOMAIN = "hu.dbpedia.org";
    private static final String ID_DBPEDIA_DOMAIN = "id.dbpedia.org";
    private static final String IT_DBPEDIA_DOMAIN = "it.dbpedia.org";
    private static final String JA_DBPEDIA_DOMAIN = "ja.dbpedia.org";
    private static final String KO_DBPEDIA_DOMAIN = "ko.dbpedia.org";
    private static final String NL_DBPEDIA_DOMAIN = "nl.dbpedia.org";
    private static final String PL_DBPEDIA_DOMAIN = "pl.dbpedia.org";
    private static final String PT_DBPEDIA_DOMAIN = "pt.dbpedia.org";
    private static final String RU_DBPEDIA_DOMAIN = "ru.dbpedia.org";
    private static final String TR_DBPEDIA_DOMAIN = "tr.dbpedia.org";

    private static final String DBPEDIA_PATH = "/resource/";

    private static final String BG_WIKIPEDIA_DOMAIN = "bg.wikipedia.org";
    private static final String CA_WIKIPEDIA_DOMAIN = "ca.wikipedia.org";
    private static final String CS_WIKIPEDIA_DOMAIN = "cs.wikipedia.org";
    private static final String DE_WIKIPEDIA_DOMAIN = "de.wikipedia.org";
    private static final String EN_WIKIPEDIA_DOMAIN = "en.wikipedia.org";
    private static final String ES_WIKIPEDIA_DOMAIN = "es.wikipedia.org";
    private static final String EU_WIKIPEDIA_DOMAIN = "eu.wikipedia.org";
    private static final String FR_WIKIPEDIA_DOMAIN = "fr.wikipedia.org";
    private static final String HU_WIKIPEDIA_DOMAIN = "hu.wikipedia.org";
    private static final String ID_WIKIPEDIA_DOMAIN = "id.wikipedia.org";
    private static final String IT_WIKIPEDIA_DOMAIN = "it.wikipedia.org";
    private static final String JA_WIKIPEDIA_DOMAIN = "ja.wikipedia.org";
    private static final String KO_WIKIPEDIA_DOMAIN = "ko.wikipedia.org";
    private static final String NL_WIKIPEDIA_DOMAIN = "nl.wikipedia.org";
    private static final String PL_WIKIPEDIA_DOMAIN = "pl.wikipedia.org";
    private static final String PT_WIKIPEDIA_DOMAIN = "pt.wikipedia.org";
    private static final String RU_WIKIPEDIA_DOMAIN = "ru.wikipedia.org";
    private static final String TR_WIKIPEDIA_DOMAIN = "tr.wikipedia.org";

    private static final String WIKIPDIA_PATH = "/wiki/";

    @Override
    public Set<String> retrieveSameURIs(String uri) {
        return retrieveSameURIs(SimpleDomainExtractor.extractDomain(uri), uri);
    }

    @Override
    public Set<String> retrieveSameURIs(String domain, String uri) {
        if ((domain == null) || (uri == null)) {
            return null;
        }
        switch (domain) {
        /*
         * DBpedia domains
         */
        case BG_DBPEDIA_DOMAIN:
            return replaceDomain(BG_DBPEDIA_DOMAIN, BG_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case CA_DBPEDIA_DOMAIN:
            return replaceDomain(CA_DBPEDIA_DOMAIN, CA_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case CS_DBPEDIA_DOMAIN:
            return replaceDomain(CS_DBPEDIA_DOMAIN, CS_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case DE_DBPEDIA_DOMAIN:
            return replaceDomain(DE_DBPEDIA_DOMAIN, DE_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case EN_DBPEDIA_DOMAIN:
            return replaceDomain(EN_DBPEDIA_DOMAIN, EN_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case ES_DBPEDIA_DOMAIN:
            return replaceDomain(ES_DBPEDIA_DOMAIN, ES_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case EU_DBPEDIA_DOMAIN:
            return replaceDomain(EU_DBPEDIA_DOMAIN, EU_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case FR_DBPEDIA_DOMAIN:
            return replaceDomain(FR_DBPEDIA_DOMAIN, FR_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case HU_DBPEDIA_DOMAIN:
            return replaceDomain(HU_DBPEDIA_DOMAIN, HU_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case ID_DBPEDIA_DOMAIN:
            return replaceDomain(ID_DBPEDIA_DOMAIN, ID_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case IT_DBPEDIA_DOMAIN:
            return replaceDomain(IT_DBPEDIA_DOMAIN, IT_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case JA_DBPEDIA_DOMAIN:
            return replaceDomain(JA_DBPEDIA_DOMAIN, JA_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case KO_DBPEDIA_DOMAIN:
            return replaceDomain(KO_DBPEDIA_DOMAIN, KO_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case NL_DBPEDIA_DOMAIN:
            return replaceDomain(NL_DBPEDIA_DOMAIN, NL_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case PL_DBPEDIA_DOMAIN:
            return replaceDomain(PL_DBPEDIA_DOMAIN, PL_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case PT_DBPEDIA_DOMAIN:
            return replaceDomain(PT_DBPEDIA_DOMAIN, PT_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case RU_DBPEDIA_DOMAIN:
            return replaceDomain(RU_DBPEDIA_DOMAIN, RU_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        case TR_DBPEDIA_DOMAIN:
            return replaceDomain(TR_DBPEDIA_DOMAIN, TR_WIKIPEDIA_DOMAIN, DBPEDIA_PATH, WIKIPDIA_PATH, uri);
        /*
         * Wikipedia domains
         */
        case BG_WIKIPEDIA_DOMAIN:
            return replaceDomain(BG_WIKIPEDIA_DOMAIN, BG_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case CA_WIKIPEDIA_DOMAIN:
            return replaceDomain(CA_WIKIPEDIA_DOMAIN, CA_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case CS_WIKIPEDIA_DOMAIN:
            return replaceDomain(CS_WIKIPEDIA_DOMAIN, CS_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case DE_WIKIPEDIA_DOMAIN:
            return replaceDomain(DE_WIKIPEDIA_DOMAIN, DE_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case EN_WIKIPEDIA_DOMAIN:
            return replaceDomain(EN_WIKIPEDIA_DOMAIN, EN_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case ES_WIKIPEDIA_DOMAIN:
            return replaceDomain(ES_WIKIPEDIA_DOMAIN, ES_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case EU_WIKIPEDIA_DOMAIN:
            return replaceDomain(EU_WIKIPEDIA_DOMAIN, EU_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case FR_WIKIPEDIA_DOMAIN:
            return replaceDomain(FR_WIKIPEDIA_DOMAIN, FR_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case HU_WIKIPEDIA_DOMAIN:
            return replaceDomain(HU_WIKIPEDIA_DOMAIN, HU_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case ID_WIKIPEDIA_DOMAIN:
            return replaceDomain(ID_WIKIPEDIA_DOMAIN, ID_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case IT_WIKIPEDIA_DOMAIN:
            return replaceDomain(IT_WIKIPEDIA_DOMAIN, IT_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case JA_WIKIPEDIA_DOMAIN:
            return replaceDomain(JA_WIKIPEDIA_DOMAIN, JA_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case KO_WIKIPEDIA_DOMAIN:
            return replaceDomain(KO_WIKIPEDIA_DOMAIN, KO_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case NL_WIKIPEDIA_DOMAIN:
            return replaceDomain(NL_WIKIPEDIA_DOMAIN, NL_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case PL_WIKIPEDIA_DOMAIN:
            return replaceDomain(PL_WIKIPEDIA_DOMAIN, PL_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case PT_WIKIPEDIA_DOMAIN:
            return replaceDomain(PT_WIKIPEDIA_DOMAIN, PT_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case RU_WIKIPEDIA_DOMAIN:
            return replaceDomain(RU_WIKIPEDIA_DOMAIN, RU_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        case TR_WIKIPEDIA_DOMAIN:
            return replaceDomain(TR_WIKIPEDIA_DOMAIN, TR_DBPEDIA_DOMAIN, WIKIPDIA_PATH, DBPEDIA_PATH, uri);
        default: {
            return null;
        }
        }
    }

    private Set<String> replaceDomain(String oldDomain, String newDomain, String oldPath, String newPath, String uri) {
        int pos = uri.indexOf(oldDomain);
        if (pos < 0) {
            return null;
        }
        pos += oldDomain.length();
        // check that the expected path is there
        if (!uri.substring(pos).startsWith(oldPath)) {
            return null;
        }
        pos += oldPath.length();
        StringBuilder builder = new StringBuilder();
        builder.append(URI_PROTOCOL_PART);
        builder.append(newDomain);
        builder.append(newPath);
        builder.append(uri.substring(pos));

        Set<String> result = new HashSet<String>();
        result.add(builder.toString());
        return result;
    }

    public void addToManager(DomainBasedSameAsRetrieverManager manager) {
        manager.addDomainSpecificRetriever(BG_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(CA_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(CS_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(DE_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(EN_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(ES_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(EU_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(FR_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(HU_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(ID_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(IT_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(JA_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(KO_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(NL_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(PL_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(PT_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(RU_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(TR_DBPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(BG_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(CA_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(CS_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(DE_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(EN_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(ES_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(EU_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(FR_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(HU_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(ID_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(IT_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(JA_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(KO_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(NL_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(PL_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(PT_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(RU_WIKIPEDIA_DOMAIN, this);
        manager.addDomainSpecificRetriever(TR_WIKIPEDIA_DOMAIN, this);
    }
}
