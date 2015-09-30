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
package org.aksw.gerbil.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SimpleThreadObserver implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleThreadObserver.class);

    private static final long REPORTING_TIME = 600000; // report every 10 minutes

    public static boolean canObserveThread() {
        return LOGGER.isInfoEnabled();
    }

    private Thread thread;

    public SimpleThreadObserver(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        LOGGER.info("Starting observation of thread {}", thread.getId());
        StackTraceElement stack[];
        StringBuilder stackString = new StringBuilder();
        do {
            stack = thread.getStackTrace();
            for (int i = 0; i < stack.length; ++i) {
                stackString.append("\n\t");
                stackString.append(stack[i]);
            }
            LOGGER.info("Status of Thread {}: isAlive={}, isInterrupted={}, state={}, stack trace:{}", thread.getId(),
                    thread.isAlive(), thread.isInterrupted(), thread.getState(), stackString.toString());
            stackString.delete(0, stackString.length());
            try {
                thread.join(REPORTING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (thread.isAlive());
        LOGGER.info("Thread {} is dead. Stopping observation.", thread.getId());
    }
}
