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
package org.aksw.gerbil.bat.datasets;

import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileBasedNIFDataset extends AbstractNIFDataset {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedNIFDataset.class);

    private String filePath;
    private Lang language;

    public FileBasedNIFDataset(WikipediaApiInterface wikiApi, String filePath, String name,
            Lang language) {
        super(wikiApi, name);
        this.filePath = filePath;
        this.language = language;
    }

    @Override
    protected InputStream getDataAsInputStream() {
        FileInputStream fin = null;
        try {
            LOGGER.debug("Loading NIF dataset from {}", filePath);
            fin = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            LOGGER.error("Couldn't load NIF dataset from file.", e);
        }
        return fin;
    }

    @Override
    protected Lang getDataLanguage() {
        return language;
    }

}
