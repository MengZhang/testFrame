package org.agmip.utility.testframe.comparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import org.agmip.common.Functions;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract test comparator which maintain the generic functions and feature
 * for all particular comparators.
 *
 * @author Meng Zhang
 */
public abstract class TestComparator {

    public enum Type {
        FILE, FOLDER
    }

    protected File expected;
    protected File actual;
    protected HashMap<String, ArrayList<Diff>> diffs = new HashMap();
    protected String title = "Unknown";
    protected String outputDir; //TODO not used yet
    protected Type compareType;
    private static final Logger LOG = LoggerFactory.getLogger(TestComparator.class);

    public TestComparator(File expectedDir, File actualDir) throws Exception {
        this.expected = expectedDir;
        this.actual = actualDir;
        compareType = null;
        outputDir = actualDir.getParent();
    }

    public File getExpected() {
        return this.expected;
    }

    public File getActual() {
        return this.actual;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputDir() {
        return this.outputDir;
    }

    public boolean getLastCompareResult() {
        return diffs.isEmpty();
    }

    public abstract HashMap<File, File> getDiffFiles();

    public abstract boolean compare() throws Exception;

    public HashMap toMap() {
        HashMap ret = new HashMap();
        ret.put("title", title);
        ret.put("compare_type", compareType.toString());
        ret.put("expected", expected.getPath());
        ret.put("actual", actual.getPath());
        ret.put("output_dir", outputDir);
        return ret;
    }

    public abstract HashMap<String, ArrayList<Diff>> getDiffs();

    public File getReport() {
        File ret = new File(Functions.revisePath(outputDir) + title + "-Diff_Report.html");
        Velocity.init();
        VelocityContext context = new VelocityContext();
        FileWriter writer;
        try {
            context.put("diffs", diffs.entrySet());
            context.put("title", title);
            writer = new FileWriter(ret);
            Reader reader = new InputStreamReader(getClass().getResourceAsStream("/compare_report.html"));
            Velocity.evaluate(context, writer, "Generate compare report", reader);
            writer.flush();
            writer.close();
            reader.close();
        } catch (IOException ex) {
            LOG.error(Functions.getStackTrace(ex));
        }
        return ret;
    }
}
