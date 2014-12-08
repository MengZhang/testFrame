package org.agmip.utility.testframe.comparator;

import java.io.File;
import java.lang.reflect.Constructor;

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
        Constructor[] cons = {
            AcmoCsvFileComparator.class.getDeclaredConstructor(File.class, File.class),
            CsvFileComparator.class.getDeclaredConstructor(File.class, File.class)
        };
        for (Constructor<FileComparator> con : cons) {
            try {
                return con.newInstance(exp, act);
            } catch (Exception e) {
            }
        }
        return new FileComparator(exp, act);
    }
}
