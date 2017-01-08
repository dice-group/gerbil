package org.aksw.gerbil.web.config.check;

/**
 * A simple interface for a class that offers a {@link #check(Object...)} method
 * that checks the given objects, i.e., files regarding their existence, and
 * returns <code>true</code> or <code>false</code>.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface Checker {

    /**
     * Checks the given objects and returns <code>true</code> if the check was
     * successful. Note that the semantic of the given object(s) is defined by
     * the implementing classes.
     * 
     * @param objects
     *            the object(s) that should be checked
     * @return <code>true</code> if the check was successful.
     */
    public boolean check(Object... objects);
}
