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
package org.aksw.gerbil.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush().
 * 
 * Main parts copied from
 * https://blogs.oracle.com/nickstephen/entry/java_redirecting_system_out_and
 */
@Deprecated
public class ConsoleLogger extends ByteArrayOutputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogger.class);

    private String lineSeparator = System.getProperty("line.separator");
    private boolean logAsError = false;

    public ConsoleLogger(boolean logAsError) {
        super();
        this.logAsError = logAsError;
    }

    /**
     * upon flush() write the existing contents of the OutputStream to the
     * logger as a log record.
     * 
     * @throws java.io.IOException
     *             in case of error
     */
    public void flush() throws IOException {
        String record;
        synchronized (this) {
            super.flush();
            record = this.toString();
            super.reset();

            if (record.length() == 0 || record.equals(lineSeparator)) {
                // avoid empty records
                return;
            }

            if (logAsError) {
                LOGGER.error(record);
            } else {
                LOGGER.info(record);
            }
        }
    }
}
