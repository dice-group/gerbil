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
package org.aksw.gerbil.semantic.kb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteListBasedUriKBClassifier extends AbstractWhiteListBasedUriKBClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhiteListBasedUriKBClassifier.class);

    public static WhiteListBasedUriKBClassifier create(File file) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return create(is);
        } catch (IOException e) {
            LOGGER.error("Exception while trying to read knowledge base namespaces.", e);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static WhiteListBasedUriKBClassifier create(InputStream stream) {
        try {
            return new WhiteListBasedUriKBClassifier(IOUtils.readLines(stream));
        } catch (IOException e) {
            LOGGER.error("Exception while trying to read knowledge base namespaces.", e);
            return null;
        }
    }

    protected WhiteListBasedUriKBClassifier(List<String> kbNamespaces) {
        super(kbNamespaces);
    }
}
