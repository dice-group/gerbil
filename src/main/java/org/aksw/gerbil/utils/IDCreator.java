package org.aksw.gerbil.utils;

import java.util.Calendar;

public class IDCreator {

    private static final String ID_FORMAT = "%1$tY%1$tm%1$td%2$04d";
    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

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
        lastTimeStamp.set(Calendar.HOUR, 0);
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
        if ((timestamp.getTimeInMillis() - lastTimeStamp.getTimeInMillis()) < ONE_DAY_IN_MILLIS) {
            return count++;
        } else {
            lastTimeStamp.set(Calendar.DAY_OF_MONTH, timestamp.get(Calendar.DAY_OF_MONTH));
            lastTimeStamp.set(Calendar.MONTH, timestamp.get(Calendar.MONTH));
            lastTimeStamp.set(Calendar.YEAR, timestamp.get(Calendar.YEAR));
            count = 1;
            return 0;
        }
    }
}
