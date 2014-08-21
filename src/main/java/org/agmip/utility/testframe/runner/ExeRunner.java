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
    public boolean run() throws IOException {
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
