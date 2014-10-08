package org.agmip.utility.testframe;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.agmip.utility.testframe.comparator.FileComparator;
import org.agmip.utility.testframe.comparator.FileComparatorFactory;
import org.agmip.utility.testframe.comparator.FolderComparator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 * @author Meng Zhang
 */
public class ComparatorTest {

    URL expectedFile;
    URL actualFile;
    URL expectedDir;
    URL actualDir;

    /**
     * setup the test data
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        expectedFile = this.getClass().getResource("/expected.txt");
        actualFile = this.getClass().getResource("/actual.txt");
        expectedDir = this.getClass().getResource("/expected");
        actualDir = this.getClass().getResource("/actual");
    }

    /**
     * test the functionality of general file comparator
     *
     * @throws java.lang.Exception
     */
    @Test
    @Ignore
    public void testFileComparator() throws Exception {
        FileComparator c = FileComparatorFactory.getFileComparator(expectedFile.getPath(), actualFile.getPath());
        assertEquals(false, c.compare());
        for (String diff : c.getSingleDiff()) {
            System.out.println(diff);
        }
    }

    /**
     * test the functionality of general folder comparator
     *
     * @throws java.lang.Exception
     */
    @Test
//    @Ignore
    public void testFolderomparator() throws Exception {
        FolderComparator c = new FolderComparator(expectedDir.getPath(), actualDir.getPath());
        assertEquals(false, c.compare());
        HashMap<String, ArrayList<String>> diffs = c.getDiff();
        for (Map.Entry<String, ArrayList<String>> diff : diffs.entrySet()) {
            System.out.println(diff.getKey());
            for (String change : diff.getValue()) {
                System.out.print("\t");
                System.out.println(change);
            }
        }
    }
}
