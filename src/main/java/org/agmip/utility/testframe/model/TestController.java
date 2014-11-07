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
import org.agmip.utility.testframe.comparator.CsvFileComparator;
import org.agmip.utility.testframe.comparator.Diff;
import org.agmip.utility.testframe.comparator.TestComparator;
import org.agmip.utility.testframe.runner.AppRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    protected ArrayList<AppRunner> runners;
    protected ArrayList<TestComparator> comparators;

    public TestController() {
        runners = new ArrayList();
        comparators = new ArrayList();
    }

//    public TestController(ArrayList<TestRunner> runners) {
//        if (runners != null) {
//            this.runners = runners;
//        } else {
//            this.runners = new ArrayList();
//        }
//    }

    @Deprecated
    public void addAppRunner(AppRunner runner) {
        runners.add(runner);
    }
    
    public void readDefJson(File testFlowDefJson) throws Exception {
        readDefJsonStr(readJsonFile(testFlowDefJson));
    }

    public void readDefJsonStr(String testFlowDefJson) throws Exception {
        HashMap def;
        try {
            def = JSONAdapter.fromJSON(testFlowDefJson);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return;
        }
        readDefMap(def);
    }

    public void readDefMap(HashMap def) throws Exception {
        this.runners.clear();
        this.comparators.clear();
        // Read Test Runner Definitions
        ArrayList<HashMap> runnerDefs = MapUtil.getObjectOr(def, "test_apps", new ArrayList<HashMap>());
        ArrayList<HashMap> comparatorDefs = MapUtil.getObjectOr(def, "comparators", new ArrayList<HashMap>());
        HashMap<String, Integer> runnerIds = new HashMap();
        for (int i = 0; i < runnerDefs.size(); i++) {
            // Initial test runner
            HashMap runnerDef = runnerDefs.get(i);
            String runnerType = MapUtil.getValueOr(runnerDef, "runner_type", "");
            String executablePath = getRelativePath(runnerDef, "executable_path", "", runnerIds);
            String workDir = getRelativePath(runnerDef, "work_dir", "", runnerIds);
            String outputDir = getRelativePath(runnerDef, "output_dir", "", runnerIds);
            AppRunner.Type type = AppRunner.Type.valueOf(runnerType.toUpperCase());
            AppRunner runner = TestDefBuilder.buildAppRunner(type, executablePath, workDir, outputDir);
//            if (type.equals(TestRunner.Type.QUADUI)) {
//                runner = new QuadUIJarRunner(executablePath, workDir, outputDir);
//            } else if (runnerType.equals(TestRunner.Type.JAR.toString()) || executablePath.toLowerCase().endsWith(".jar")) {
//                runner = new JarRunner(executablePath, workDir, outputDir);
//            } else if (runnerType.equals(TestRunner.Type.APSIM.toString())) {
//                runner = new ApsimRunner(executablePath, workDir, outputDir);
//            } else {
//                runner = new ExeRunner(executablePath, workDir, outputDir);
//            }
            // Register the title
            String title = MapUtil.getValueOr(runnerDef, "title", "");
            if (!title.equals("")) {
                runner.setTitle(title);
            } else {
                title = runner.getTitle();
            }
            runnerIds.put(title, i);
            // Set regular arguments
            ArrayList<String> args = new ArrayList();
            ArrayList<String> preArgs = MapUtil.getObjectOr(runnerDef, "arguments", new ArrayList<String>());
            for (String arg : preArgs) {
                args.add(getRelativePath(arg, runnerIds));
            }
            runner.setArguments(args);
            preArgs.clear();
            // Set app-specific arguments
            HashMap<String, String> spcOpts = MapUtil.getObjectOr(runnerDef, "specific_arguments", new HashMap());
            for (Entry<String, String> e : spcOpts.entrySet()) {
                String optName = e.getKey();
                String optValue = getRelativePath(e.getValue(), runnerIds);
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
        // Read Comparator Definitions
        for (HashMap comparatorDef : comparatorDefs) {
            String compareType = MapUtil.getValueOr(comparatorDef, "compare_type", "");
            String expectedPath = MapUtil.getValueOr(comparatorDef, "expected", "");
            String actualPath = getRelativePath(comparatorDef, "actual", "", runnerIds);
            TestComparator.Type type = TestComparator.Type.valueOf(compareType.toUpperCase());
            TestComparator comparator = TestDefBuilder.buildTestComparator(type, expectedPath, actualPath);
            // Register the title
            comparator.setTitle(MapUtil.getValueOr(comparatorDef, "title", ""));
            comparator.setOutputDir(getRelativePath(comparatorDef, "output_dir", "", runnerIds));
            // Read CSV comparator feature
            if (comparator instanceof CsvFileComparator) {
                CsvFileComparator com = (CsvFileComparator) comparator;
                com.setSpliter(MapUtil.getValueOr(comparatorDef, "spliter", ","));
                com.setHeaderRawNum(MapUtil.getObjectOr(comparatorDef, "headerRawNum", 0));
                com.setKeyHeaderName((String) comparatorDef.get("keyHeaderName"));
                com.setCompareHeaderNames((ArrayList)comparatorDef.get("compareHeaderNames"));
            }
            comparators.add(comparator);
        }
    }

    public void run() throws Exception {
        for (AppRunner runner : runners) {
            if (runner.run() != 0) {
                LOG.info("Error detected, test is terminated!");
                throw new Exception(runner.getTitle() + " is failed");
            } else {
            }
        }
        LOG.info("All tests finished!");
        LOG.info("Start compare simulation results...");
        for (TestComparator comparator : comparators) {
            if (comparator.compare()) {
                LOG.info("Test result match with expected result for [{}]", comparator.getTitle());
            } else {
                handleCompareResult(comparator);
            }
        }
    }
    
    public ArrayList<TestComparator> getComparators() {
        return this.comparators;
    }

    private String readJsonFile(File f) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();
            return sb.toString();
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return "";
        }
    }

    private String getRelativePath(HashMap runnerDef, String id, String defVal, HashMap<String, Integer> runnerIds) throws Exception {
        return getRelativePath(MapUtil.getValueOr(runnerDef, id, defVal), runnerIds);
    }

    private String getRelativePath(String path, HashMap<String, Integer> runnerIds) throws Exception {
        if (path.startsWith("-") || !path.contains("*")) {
            return path;
        } else if (!path.matches(".*\\*\\w+\\..+")) {
            throw new Exception("Invalid format for relative path : [" + path + "]");
        } else {
            int startIdx = path.indexOf("*");
            String rltPath = path.substring(startIdx + 1);
            String rltTitle = rltPath.substring(0, rltPath.indexOf("."));
            int endIdx = rltPath.indexOf(File.separator);
            if (endIdx == -1) {
                endIdx = rltPath.length();
            }
            String rltVar = rltPath.substring(rltPath.indexOf(".") + 1, endIdx);
            endIdx += startIdx + 1;
            if (!runnerIds.containsKey(rltTitle)) {
                throw new Exception("Invalid format for relative path : [" + path + "]");
            } else {
                AppRunner r = this.runners.get(runnerIds.get(rltTitle));
                String rltVal;
                if (rltVar.equalsIgnoreCase("work_dir")) {
                    rltVal = r.getWorkDir().getPath();
                } else if (rltVar.equalsIgnoreCase("output_dir")) {

                    if (r.getOutputDir().isAbsolute()) {
                        rltVal = r.getOutputDir().getPath();
                    } else {
                        rltVal = r.getWorkDir().getPath() + File.separator + r.getOutputDir().getPath();
                    }
                } else {
                    throw new Exception("Invalid format for relative path : [" + path + "]");
                }
                path = path.substring(0, startIdx) + rltVal + path.substring(endIdx);
            }

            return path;
        }
    }

    private void handleCompareResult(TestComparator comparator) {
        LOG.info("Test result not match with expected result for {}", comparator.getTitle());
        HashMap result = comparator.getDiffs();
        for (Object fileName : result.keySet()) {
            System.out.println("*** Difference for " + fileName + " file ***");
            ArrayList<Diff> diffs = (ArrayList<Diff>) result.get(fileName);
            for (Diff diff : diffs) {
                LOG.info(diff.getHtmlString());
            }
        }
    }
}
