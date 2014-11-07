package org.agmip.utility.testframe.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.agmip.common.Functions;
import org.agmip.util.JSONAdapter;
import org.agmip.utility.testframe.comparator.FileComparatorFactory;
import org.agmip.utility.testframe.comparator.FolderComparator;
import org.agmip.utility.testframe.comparator.TestComparator;
import org.agmip.utility.testframe.runner.ApsimRunner;
import org.agmip.utility.testframe.runner.ExeRunner;
import org.agmip.utility.testframe.runner.JarRunner;
import org.agmip.utility.testframe.runner.QuadUIJarRunner;
import org.agmip.utility.testframe.runner.AppRunner;
import org.agmip.utility.testframe.runner.DssatRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class TestDefBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TestDefBuilder.class);
    protected ArrayList<AppRunner> runners = new ArrayList();
    protected ArrayList<TestComparator> comparators = new ArrayList();

    public HashMap<String, ArrayList<HashMap>> getDef() {
        HashMap<String, ArrayList<HashMap>> def = new HashMap();
        ArrayList<HashMap> runnersM = new ArrayList();
        ArrayList<HashMap> comparatorsM = new ArrayList();
        for (AppRunner runner : runners) {
            runnersM.add(runner.toMap());
        }
        for (TestComparator comparator : comparators) {
            comparatorsM.add(comparator.toMap());
        }
        def.put("test_apps", runnersM);
        def.put("comparators", comparatorsM);
        return def;
    }

    public String toJsonStr() throws IOException {
        return JSONAdapter.toJSON(getDef());
    }

    public void toJsonFile(File jsonFile) throws IOException {
        Functions.revisePath(jsonFile.getParent());
        BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
        bw.write(toJsonStr());
        bw.flush();
        bw.close();
    }

    public AppRunner addAppRunner(AppRunner.Type runnerType, String executablePath, String workDir, String outputDir) {
        AppRunner runner = buildAppRunner(runnerType, executablePath, workDir, outputDir);
        runners.add(runner);
        return runner;
    }
    
    public AppRunner addAppRunner(AppRunner.Type runnerType, String executablePath) {
        return addAppRunner(runnerType, executablePath, "", "");
    }

    public static AppRunner buildAppRunner(AppRunner.Type runnerType, String executablePath, String workDir, String outputDir) {
        AppRunner runner;
        if (runnerType.equals(AppRunner.Type.QUADUI)) {
            runner = new QuadUIJarRunner(executablePath, workDir, outputDir);
        } else if (runnerType.equals(AppRunner.Type.JAR) || executablePath.toLowerCase().endsWith(".jar")) {
            runner = new JarRunner(executablePath, workDir, outputDir);
        } else if (runnerType.equals(AppRunner.Type.DSSAT)) {
            runner = new DssatRunner(executablePath, workDir, outputDir);
        } else if (runnerType.equals(AppRunner.Type.APSIM)) {
            runner = new ApsimRunner(executablePath, workDir, outputDir);
        } else {
            runner = new ExeRunner(executablePath, workDir, outputDir);
        }
        return runner;
    }

    public TestComparator addTestComparator(TestComparator.Type compareType, String expectedPath, String actualPath) throws Exception {
        TestComparator comparator = buildTestComparator(compareType, expectedPath, actualPath);
        comparators.add(comparator);
        return comparator;
    }

    public static TestComparator buildTestComparator(TestComparator.Type compareType, String expectedPath, String actualPath) throws Exception {
        TestComparator comparator;
        if (compareType.equals(TestComparator.Type.FOLDER)) {
            comparator = new FolderComparator(expectedPath, actualPath);
        } else {
            comparator = FileComparatorFactory.getFileComparator(expectedPath, actualPath);
        }
        return comparator;
    }
}
