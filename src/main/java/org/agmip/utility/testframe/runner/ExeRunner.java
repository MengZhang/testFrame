package org.agmip.utility.testframe.runner;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ExeRunner extends TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ExeRunner.class);

    public ExeRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
    }

    public ExeRunner(String executablePath) {
        super(executablePath, "", "");
    }

    @Override
    public void run() throws IOException {
        LOG.info("Run {}...", executablePath.getName());
        Process p = getProcess();
        printSubProcessLog(p, LOG);
        String executableName = executablePath.getName();
        try {
            LOG.info("{} ends with code [{}]", executableName, p.waitFor());
        } catch (InterruptedException ex) {
            LOG.error("{} got error: [{}]", executableName, ex.getMessage());
        }
    }
}
