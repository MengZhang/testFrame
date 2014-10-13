package org.agmip.utility.testframe.runner;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General Jar runner for non-specific software or component
 *
 * @author Meng Zhang
 */
public class JarRunner extends AppRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JarRunner.class);

    public JarRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
        runnerType = Type.JAR;
    }

    public JarRunner(String executablePath) {
        super(executablePath);
        runnerType = Type.JAR;
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
    public void handleOutput() {
        // TODO
    }
}
