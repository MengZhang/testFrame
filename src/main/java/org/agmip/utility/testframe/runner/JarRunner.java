package org.agmip.utility.testframe.runner;

import java.io.IOException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General Jar runner for non-specific software or component
 *
 * @author Meng Zhang
 */
public class JarRunner extends TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JarRunner.class);

    public JarRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
    }

    public JarRunner(String executablePath) {
        super(executablePath);
    }

    @Override
    public ArrayList<String> getArgsList() {
        ArrayList<String> argsList = new ArrayList();
        argsList.add("java");
        argsList.add("-jar");
        argsList.addAll(super.getArgsList());
        return argsList;
    }

    @Override
    public final void run() throws IOException {
        LOG.info("Run {}...", executablePath.getName());
        if (isJavaInstalled()) {
            Process p = getProcess();
            printSubProcessLog(p, LOG);
            LOG.info("{} ends with code [{}]", executablePath.getName(), p.exitValue());
        } else {
            LOG.error("Java is not installed or configured correctly in the system.");
        }
    }

    protected boolean isJavaInstalled() {
        return true; // TODO
    }
}
