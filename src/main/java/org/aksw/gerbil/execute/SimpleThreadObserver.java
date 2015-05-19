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
