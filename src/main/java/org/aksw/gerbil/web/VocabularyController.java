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
package org.aksw.gerbil.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VocabularyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyController.class);

    private static final String GERBIL_VOCABULARY_JSON_FILE = "vocab/gerbil.json";
    private static final String GERBIL_VOCABULARY_RDF_FILE = "vocab/gerbil.rdf";
    private static final String GERBIL_VOCABULARY_TTL_FILE = "vocab/gerbil.ttl";

    @RequestMapping(value = "/vocab*", produces = { "text/turtle", "text/plain" })
    public @ResponseBody
    String vocabularyAsTTL() {
        return getResourceAsString(GERBIL_VOCABULARY_TTL_FILE);
    }
    
    @RequestMapping(value = "/vocab*", produces = { "application/json+ld", "application/json" })
    public @ResponseBody
    String vocabularyAsJSON() {
        return getResourceAsString(GERBIL_VOCABULARY_JSON_FILE);
    }

    @RequestMapping(value = "/vocab*", produces = "application/rdf+xml")
    public @ResponseBody
    String vocabularyAsRDFXML() {
        return getResourceAsString(GERBIL_VOCABULARY_RDF_FILE);
    }

    private String getResourceAsString(String resource) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        if (is != null) {
            try {
                return IOUtils.toString(is, "utf-8");
            } catch (IOException e) {
                LOGGER.error("Exception while loading vocabulary resource. Returning null.", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return null;
    }
}
