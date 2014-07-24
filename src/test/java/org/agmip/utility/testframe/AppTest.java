package org.agmip.utility.testframe;

import java.io.File;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.agmip.utility.testframe.model.TestController;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     *
     * @throws java.io.IOException
     */
    public void testApp() throws IOException {
        TestController c = new TestController();
        c.readDefJson(new File(this.getClass().getResource("/testFlow.json").getPath()));
        c.run();
    }
}
