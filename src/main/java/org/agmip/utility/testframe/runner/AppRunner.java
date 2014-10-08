package org.agmip.utility.testframe.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public abstract class AppRunner {

    public enum Type {
        EXE, JAR, QUADUI, DSSAT, APSIM
    }

    private static final Logger LOG = LoggerFactory.getLogger(AppRunner.class);

    protected String title;
    protected File executablePath;
    protected File workDir;
    protected File outputDir;
    protected ArrayList<String> arguments;
    protected Type runnerType;
    protected static final int FATAL_ERROR_RET = -99;

    public AppRunner(String executablePath, String workPath, String outputPath) {
        this.executablePath = new File(executablePath);
        this.workDir = new File(workPath);
        this.outputDir = new File(outputPath);
        this.arguments = new ArrayList();
        this.title = this.executablePath.getName();
        runnerType = null;
    }

    public AppRunner(String executablePath) {
        this(executablePath, "", "");
    }

    public File getWorkDir() {
        return workDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setArguments(String optStr) {
        this.arguments.clear();
        if (optStr == null || optStr.equals("")) {
            return;
        }
        String[] opts = optStr.split(" ");
        if (opts != null) {
            this.arguments.addAll(Arrays.asList(opts));
        }
    }

    public void setArguments(String... arguments) {
        this.arguments.clear();
        if (arguments != null) {
            this.arguments.addAll(Arrays.asList(arguments));
        }
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments.clear();
        if (arguments != null) {
            this.arguments.addAll(arguments);
        }
    }

    public ArrayList<String> getArguments() {
        return this.arguments;
    }

    protected ArrayList<String> getProcessArguments() {
        ArrayList<String> argsList = new ArrayList();
        argsList.add(executablePath.getPath());
        if (arguments != null) {
            argsList.addAll(arguments);
        }
//        argsList.add(outputDir.getPath());

        return argsList;
    }

    protected Process getProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(getProcessArguments());
        if (!workDir.getPath().equals("")) {
            pb.directory(workDir);
        }
        return pb.start();
    }

    protected void printSubProcessLog(Process p, Logger LOG) throws IOException {
        BufferedReader brInfo = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = brInfo.readLine()) != null) {
            LOG.info("{}: {}", getTitle(), line);
        }
    }

    public HashMap toMap() {
        HashMap ret = new HashMap();
        ret.put("title", title);
        ret.put("runner_type", runnerType.toString());
        ret.put("executable_path", executablePath.getPath());
        ret.put("work_dir", workDir.getPath());
        ret.put("output_dir", outputDir.getPath());
        ret.put("arguments", arguments);
        return ret;
    }
    
    public abstract void handleOutput();

    public int run() throws IOException {
        LOG.info("Run {}...", getTitle());
        Process p = getProcess();
        printSubProcessLog(p, LOG);
        try {
            int ret = p.waitFor();
            LOG.info("{} ends with code [{}]", getTitle(), ret);
            handleOutput();
            return ret;
        } catch (InterruptedException ex) {
            LOG.error("{} got error: [{}]", getTitle(), ex.getMessage());
            return FATAL_ERROR_RET;
        }
    }
}
