package org.agmip.utility.testframe.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public abstract class TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

    protected File executablePath;
    protected File workDir;
    protected File outputDir;
    protected ArrayList<String> options;

    public TestRunner(String executablePath, String workPath, String outputPath) {
        this.executablePath = new File(executablePath);
        this.workDir = new File(workPath);
        this.outputDir = new File(outputPath);
        this.options = new ArrayList();
    }

    public TestRunner(String executablePath) {
        this(executablePath, "", "");
    }

    public File getWorkDir() {
        return workDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOptions(String optStr) {
        this.options.clear();
        String[] options = optStr.split(" ");
        if (options != null) {
            this.options.addAll(Arrays.asList(options));
        }
    }

    public void setOptions(String... options) {
        this.options.clear();
        if (options != null) {
            this.options.addAll(Arrays.asList(options));
        }
    }

    public ArrayList<String> getArgsList() {
        ArrayList<String> argsList = new ArrayList();
        argsList.add(executablePath.getPath());
        if (options != null) {
            argsList.addAll(options);
        }
        argsList.add(outputDir.getPath());

        return argsList;
    }

    protected Process getProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(getArgsList());
        if (!workDir.getPath().equals("")) {
            pb.directory(workDir);
        }
        return pb.start();
    }

    protected void printSubProcessLog(Process p, Logger LOG) throws IOException {
        BufferedReader brInfo = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String executableName = executablePath.getName();
        while ((line = brInfo.readLine()) != null) {
            LOG.info("{}: {}", executableName, line);
        }
    }

    public abstract void run() throws IOException;
}
