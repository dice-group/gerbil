package org.aksw.gerbil.execute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
