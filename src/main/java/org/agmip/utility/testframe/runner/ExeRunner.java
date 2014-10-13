package org.agmip.utility.testframe.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ExeRunner extends AppRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ExeRunner.class);

    public ExeRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
        runnerType = Type.EXE;
    }

    public ExeRunner(String executablePath) {
        super(executablePath);
        runnerType = Type.EXE;
    }
    
    @Override
    public void handleOutput() {
        // TODO
    }
}
