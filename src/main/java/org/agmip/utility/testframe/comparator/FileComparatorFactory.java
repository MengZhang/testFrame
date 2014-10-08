package org.agmip.utility.testframe.comparator;

import java.io.File;

/**
 *
 * @author Meng Zhang
 */
public class FileComparatorFactory {

    public static FileComparator getFileComparator(String exp, String act) throws Exception {
        return getFileComparator(new File(exp), new File(act));
    }

    public static FileComparator getFileComparator(File exp, File act) throws Exception {
        String fName = exp.getName().toLowerCase();
        if (fName.endsWith(".zip")) {
            return null;
        }
        return new FileComparator(exp, act);
    }
}
