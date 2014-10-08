/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.utility.testframe.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Meng Zhang
 */
public abstract class TestComparator {
    
    public enum Type {FILE, FOLDER}

    protected File expected;
    protected File actual;
    protected HashMap<String, ArrayList<String>> diff = new HashMap();
    protected String title = "N/A";
    protected String outputDir = ""; //TODO not used yet
    protected Type compareType;

    public TestComparator(File expectedDir, File actualDir) throws Exception {
        this.expected = expectedDir;
        this.actual = actualDir;
        compareType = null;
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
        return diff.isEmpty();
    }

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

    public abstract HashMap<String, ArrayList<String>> getDiff();
}
