package org.agmip.utility.testframe.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;

/**
 *
 * @author Meng Zhang
 */
public class ApsimRunner extends ExeRunner {

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
}
