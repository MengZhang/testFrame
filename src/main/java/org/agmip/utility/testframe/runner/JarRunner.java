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
    public ArrayList<String> getProcessArguments() {
        ArrayList<String> argsList = new ArrayList();
        argsList.add("java");
        argsList.add("-jar");
        argsList.addAll(super.getProcessArguments());
        return argsList;
    }

    @Override
    public final boolean run() throws IOException {
        LOG.info("Run {}...", getTitle());
        Process p = getProcess();
        printSubProcessLog(p, LOG);
        try {
            int ret = p.waitFor();
            LOG.info("{} ends with code [{}]", getTitle(), ret);
            return ret == 0;
        } catch (InterruptedException ex) {
            LOG.error("{} got error: [{}]", getTitle(), ex.getMessage());
            return false;
        }
    }
}
