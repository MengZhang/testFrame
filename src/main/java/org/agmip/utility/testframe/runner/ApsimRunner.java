package org.agmip.utility.testframe.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ApsimRunner extends ExeRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApsimRunner.class);

    public ApsimRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
    }

    @Override
    protected void printSubProcessLog(Process p, Logger LOG) throws IOException {
        BufferedReader brInfo = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = brInfo.readLine()) != null) {
            LOG.info("{}: {}", getTitle(), line);
            if (line.contains("exiting")) {
                p.destroy();
                break;
            }
        }
    }

    @Override
    public boolean run() throws IOException {
        LOG.info("Run {}...", getTitle());
        Process p = getProcess();
        printSubProcessLog(p, LOG);
        try {
            int ret = p.waitFor();
            LOG.info("{} ends with code [{}]", getTitle(), ret);
            return ret == 0 || ret == 1;
        } catch (InterruptedException ex) {
            LOG.error("{} got error: [{}]", getTitle(), ex.getMessage());
            return false;
        }
    }
}
