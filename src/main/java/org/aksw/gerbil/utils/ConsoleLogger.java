package org.aksw.gerbil.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush().
 * 
 * Main parts copied from https://blogs.oracle.com/nickstephen/entry/java_redirecting_system_out_and
 */
public class ConsoleLogger extends ByteArrayOutputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogger.class);

    private String lineSeparator = System.getProperty("line.separator");
    private boolean logAsError = false;

    public ConsoleLogger(boolean logAsError) {
        super();
        this.logAsError = logAsError;
    }

    /**
     * upon flush() write the existing contents of the OutputStream
     * to the logger as a log record.
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
