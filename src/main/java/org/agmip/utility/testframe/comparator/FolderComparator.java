package org.agmip.utility.testframe.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Meng Zhang
 */
public class FolderComparator extends TestComparator {

    public FolderComparator(File expectedDir, File actualDir) throws Exception {
        super(expectedDir, actualDir);
        if (!expectedDir.isDirectory() && expectedDir.getName().contains(".")) {
            throw new Exception("Directory for expected result is invalid [" + expected.getName() + "]");
        } else if (!actualDir.isDirectory() && expectedDir.getName().contains(".")) {
            throw new Exception("Directory for actual result is invalid [" + actual.getName() + "]");
        }
        compareType = Type.FOLDER;
    }
    
    public FolderComparator(String expectedDir, String actualDir) throws Exception {
        this(new File(expectedDir), new File(actualDir));
    }

    @Override
    public HashMap<String, ArrayList<String>> getDiff() {
        return this.diff;
    }

    public ArrayList<String> getDiff(String fileName) {
        return this.diff.get(fileName);
    }

    @Override
    public boolean compare() throws Exception {
        // Initial the comparison
        diff = new HashMap();
        HashMap<String, File> expFiles = getSubFiles(this.expected);
        HashMap<String, File> actFiles = getSubFiles(this.actual);

        // Compare every file in the expected directory
        for (String fileName : expFiles.keySet()) {
            File expFile = expFiles.get(fileName);
            if (actFiles.containsKey(fileName)) {
                File actFile = actFiles.remove(fileName);
                FileComparator comparator = FileComparatorFactory.getFileComparator(expFile, actFile);
                if (!comparator.compare()) {
                    addDiffMsgArr(fileName, comparator.getSingleDiff());
                }
            } else {
                addDiffMsg(fileName, fileName + " is expected but missing in the result of current run.");
            }
        }

        // Check if there is any file remained in the actual result directory
        for (String fileName : actFiles.keySet()) {
            addDiffMsg(fileName, fileName + " is unexpected and existed in the result of current run .");
        }

        return diff.isEmpty();
    }

    protected HashMap<String, File> getSubFiles(File dir) {
        HashMap<String, File> ret = new HashMap();
        for (File f : dir.listFiles()) {
            ret.put(f.getName(), f);
        }
        return ret;
    }

    protected void addDiffMsg(String fileName, String msg) {
        ArrayList msgs = this.diff.get(fileName);
        if (msgs == null) {
            msgs = new ArrayList();
            this.diff.put(fileName, msgs);
        }
        msgs.add(msg);
    }

    protected void addDiffMsgArr(String fileName, ArrayList<String> msgArr) {
        ArrayList msgs = this.diff.get(fileName);
        if (msgs == null) {
            msgs = new ArrayList();
            this.diff.put(fileName, msgs);
        }
        msgs.addAll(msgArr);
    }
}
