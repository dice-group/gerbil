package org.aksw.gerbil.web.config.check;

import java.io.File;

/**
 * A {@link Checker} that checks whether the given object(s) (interpreted as
 * String) is a file and does exist.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class FileChecker implements Checker {

    @Override
    public boolean check(Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            if (!checkSingleObject(objects[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean checkSingleObject(Object object) {
        File file = null;
        if (object instanceof File) {
            file = (File) object;
        } else if (object instanceof String) {
            file = new File((String) object);
        } else {
            file = new File(object.toString());
        }
        return file.exists() && file.isFile();
    }

}
