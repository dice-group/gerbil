package org.aksw.gerbil.test;

import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.web.config.RootConfig;
import org.junit.Ignore;

/**
 * This class is used to perform the JUnit tests faster by letting them share a
 * single {@link EntityCheckerManager} instance. The instance is created by
 * calling the {@link RootConfig#getEntityCheckerManager()} method.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@Ignore
public class EntityCheckerManagerSingleton4Tests {

    private static EntityCheckerManager instance = null;

    public synchronized static EntityCheckerManager getInstance() {
        if (instance == null) {
            instance = RootConfig.getEntityCheckerManager();
        }
        return instance;
    }

}
