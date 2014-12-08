package org.agmip.utility.testframe.runner;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class DssatRunner extends ExeRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DssatRunner.class);

    public DssatRunner(String executablePath, String workDir, String outputDir) {
        super(executablePath, workDir, outputDir);
        runnerType = Type.DSSAT;
    }

    public DssatRunner(String executablePath) {
        super(executablePath);
        runnerType = Type.DSSAT;
    }

//    @Override
//    public ArrayList<String> getProcessArguments() {
//        ArrayList<String> argsList = new ArrayList();
//        argsList.addAll(super.getProcessArguments());
//        argsList.remove(argsList.size() - 1);
//        return argsList;
//    }
    @Override
    public void handleOutput() {
        // TODO
    }

    @Override
    protected Process getProcess() throws IOException {
        return getProcessByVersion();
    }
}
