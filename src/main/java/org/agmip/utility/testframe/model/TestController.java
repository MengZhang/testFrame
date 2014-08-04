package org.agmip.utility.testframe.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.agmip.util.JSONAdapter;
import org.agmip.util.MapUtil;
import org.agmip.utility.testframe.runner.ExeRunner;
import org.agmip.utility.testframe.runner.JarRunner;
import org.agmip.utility.testframe.runner.QuadUIJarRunner;
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

    public void readDefJson(File testFlowDefJson) {
        this.runners.clear();
        HashMap def;
        try {
            def = JSONAdapter.fromJSON(readJsonFile(testFlowDefJson));
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return;
        }
        ArrayList<HashMap> runnerDefs = MapUtil.getObjectOr(def, "test_apps", new ArrayList<HashMap>());
        for (HashMap runnerDef : runnerDefs) {
            TestRunner runner;
            String runnerType = MapUtil.getValueOr(runnerDef, "runner_type", "");
            String executablePath = MapUtil.getValueOr(runnerDef, "executable_path", "");
            String workDir = MapUtil.getValueOr(runnerDef, "work_dir", "");
            String outputDir = MapUtil.getValueOr(runnerDef, "output_dir", "");
            if (runnerType.equals("quadui")) {
                runner = new QuadUIJarRunner(executablePath, workDir, outputDir);
            } else if (executablePath.toLowerCase().endsWith(".jar")) {
                runner = new JarRunner(executablePath, workDir, outputDir);
            } else {
                runner = new ExeRunner(executablePath, workDir, outputDir);
            }
            runner.setArguments(MapUtil.getObjectOr(runnerDef, "arguments", new ArrayList()));
            HashMap<String, String> spcOpts = MapUtil.getObjectOr(runnerDef, "specific_arguments", new HashMap());
            for (Entry<String, String> e : spcOpts.entrySet()) {
                String optName = e.getKey();
                String optValue = e.getValue();
                String methodName = "set" + optName.substring(0, 1).toUpperCase() + optName.substring(1);
                try {
                    Method m = runner.getClass().getMethod(methodName, String.class);
                    m.invoke(runner, optValue);
                } catch (NoSuchMethodException ex) {
                    LOG.warn("Unrecognizable argument [{}] is found in the definition file for {}, will be skipped.", optName, runner.getTitle());
                    LOG.warn(ex.getMessage());
                } catch (SecurityException ex) {
                    LOG.warn("Argument [{}] is failed to be set for {}, will be skipped", optName, runner.getTitle());
                    LOG.warn(ex.getMessage());
                } catch (IllegalAccessException ex) {
                    LOG.warn(ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    LOG.warn("Argument [{}] has a illegal value [{}] for {}, will be skipped", optName, optValue, runner.getTitle());
                    LOG.warn(ex.getMessage());
                } catch (InvocationTargetException ex) {
                    LOG.warn(ex.getMessage());
                }
            }
            this.runners.add(runner);
        }
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
