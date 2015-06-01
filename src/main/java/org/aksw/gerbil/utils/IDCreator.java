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
package org.aksw.gerbil.utils;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IDCreator.class);

    private static final String ID_FORMAT = "%1$tY%1$tm%1$td%2$04d";

    private static IDCreator instance = null;

    public synchronized static IDCreator getInstance() {
        if (instance == null) {
            instance = new IDCreator();
        }
        return instance;
    }

    private int count;
    private Calendar lastTimeStamp;

    private IDCreator() {
        count = 0;
        lastTimeStamp = Calendar.getInstance();
        lastTimeStamp.set(Calendar.HOUR_OF_DAY, 0);
        lastTimeStamp.set(Calendar.MINUTE, 0);
        lastTimeStamp.set(Calendar.SECOND, 0);
        lastTimeStamp.set(Calendar.MILLISECOND, 0);
    }

    public String createID() {
        Calendar timestamp = Calendar.getInstance();
        int count = getCount(timestamp);
        return String.format(ID_FORMAT, timestamp, count);
    }

    private synchronized int getCount(Calendar timestamp) {
        // Check whether the given time stamp is still from the same day as the last one
        if ((timestamp.get(Calendar.YEAR) == lastTimeStamp.get(Calendar.YEAR))
                && (timestamp.get(Calendar.MONTH) == lastTimeStamp.get(Calendar.MONTH))
                && (timestamp.get(Calendar.DAY_OF_MONTH) == lastTimeStamp.get(Calendar.DAY_OF_MONTH))) {
            return count++;
        } else {
            lastTimeStamp.set(timestamp.get(Calendar.YEAR), timestamp.get(Calendar.MONTH),
                    timestamp.get(Calendar.DAY_OF_MONTH));
            count = 1;
            return 0;
        }
    }

    public void setLastCreatedID(String id) {
        try {
            int year = Integer.parseInt(id.substring(0, 4));
            int month = Integer.parseInt(id.substring(4, 6));
            int day = Integer.parseInt(id.substring(6, 8));
            int count = Integer.parseInt(id.substring(8));

            // the count has to be increased since it should point to the next ID
            this.count = count + 1;
            // Note that the JANUARY could be set to 0, so we have to make this silly calculation
            lastTimeStamp.set(year, Calendar.JANUARY + (month - 1), day, 0, 0, 0);
        } catch (Exception e) {
            LOGGER.error("Couldn't parse given last ID. Ignoring it.", e);
        }
    }
}
