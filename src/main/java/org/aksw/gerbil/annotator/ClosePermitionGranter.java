package org.aksw.gerbil.annotator;

import java.io.Closeable;

/**
 * Classes that are implementing this interface will be asked whether the
 * {@link Closeable} they are observing is allowed to perform a close if their
 * {@link Closeable#close()} method has been called.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public interface ClosePermitionGranter {

    /**
     * Returns true if the observed {@link Closeable} is allowed to close
     * itself.
     * 
     * @return
     */
    public boolean givePermissionToClose();
}
