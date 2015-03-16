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
@RequestMapping("/vocab")
public class VocabularyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyController.class);

    private static final String GERBIL_VOCABULARY_JSON_FILE = "vocab/gerbil.json";
    private static final String GERBIL_VOCABULARY_RDF_FILE = "vocab/gerbil.rdf";
    private static final String GERBIL_VOCABULARY_TTL_FILE = "vocab/gerbil.ttl";

    @RequestMapping(value = "/gerbil*", produces = { "text/turtle", "text/plain" })
    public @ResponseBody
    String vocabularyAsTTL() {
        return getResourceAsString(GERBIL_VOCABULARY_TTL_FILE);
    }

    @RequestMapping(value = "/gerbil*", produces = { "application/json+ld", "application/json" })
    public @ResponseBody
    String vocabularyAsJSON() {
        return getResourceAsString(GERBIL_VOCABULARY_JSON_FILE);
    }

    @RequestMapping(value = "/gerbil*", produces = "application/rdf+xml")
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
