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

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class IDCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IDCreator.class);

    private static final String ID_FORMAT = "%1$tY%1$tm%1$td%2$04d";

    private static IDCreator instance = null;

    @Bean
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
