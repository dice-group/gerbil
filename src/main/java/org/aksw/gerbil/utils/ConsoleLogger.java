/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
