package org.agmip.utility.testframe.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.agmip.util.JSONAdapter;
import org.agmip.util.MapUtil;
import org.agmip.utility.testframe.runner.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    protected ArrayList<TestRunner> runners;

    public TestController() {
        runners = new ArrayList();
    }
    
    public TestController(File testFlowDefJson) {
        HashMap def;
        try {
            def = JSONAdapter.fromJSON(readJsonFile(testFlowDefJson));
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return;
        }
        ArrayList<HashMap> runnerDefs = MapUtil.getObjectOr(def, "test_apps", new ArrayList<HashMap>());
        
    }

    public TestController(ArrayList<TestRunner> runners) {
        if (runners != null) {
            this.runners = runners;
        } else {
            this.runners = new ArrayList();
        }
    }
    
    public void addTestRunner(TestRunner runner) {
        runners.add(runner);
    }
    
    public void run() throws IOException {
        for (TestRunner runner : runners) {
            runner.run();
        }
        LOG.info("All tests finished!");
    }
    
    private String readJsonFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            return sb.toString();
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return "";
        }
    }
}
