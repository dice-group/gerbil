package org.aksw.gerbil.test;

import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.web.config.RootConfig;
import org.junit.Ignore;

/**
 * This class is used to perform the JUnit tests faster by letting them share a
 * single {@link SameAsRetriever} instance. The instance is created by calling
 * the {@link RootConfig#createSameAsRetriever()} method.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
@Ignore
public class SameAsRetrieverSingleton4Tests {

    private static SameAsRetriever instance = null;

    public synchronized static SameAsRetriever getInstance() {
        if (instance == null) {
            instance = RootConfig.createSameAsRetriever();
        }
        return instance;
    }

}
