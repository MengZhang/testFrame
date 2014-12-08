package org.agmip.utility.testframe.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.agmip.common.Functions;
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

    protected Process getProcessByVersion() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(getProcessArguments());
        if (workDir != null && outputDir != null && !workDir.equals(outputDir)) {
            pb.directory(outputDir);
            copy(workDir, outputDir);
        } else if (!workDir.getPath().equals("")) {
            pb.directory(workDir);
        }
        return pb.start();
    }

    private void copy(File orgDir, File dstDir) {
        if (!dstDir.exists()) {
            dstDir.mkdirs();
        } else {
            for (File f : outputDir.listFiles()) {
                f.delete();
            }
        }
        for (File f : orgDir.listFiles()) {
            File dstF = new File(dstDir.getPath() + File.separator + f.getName());
            if (f.isDirectory()) {
                copy(f, dstF);
            } else {
                try {
                    FileInputStream fi = new FileInputStream(f);
                    FileOutputStream fo = new FileOutputStream(dstF);
                    FileChannel in = fi.getChannel();
                    FileChannel out = fo.getChannel();
                    in.transferTo(0, in.size(), out);
                    fi.close();
                    in.close();
                    fo.close();
                    out.close();
                } catch (IOException e) {
                    Functions.getStackTrace(e);
                }
            }
        }
    }
}
