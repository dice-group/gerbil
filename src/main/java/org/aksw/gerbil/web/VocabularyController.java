/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
