package org.agmip.utility.testframe.comparator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.agmip.util.MapUtil;

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
    public HashMap<String, ArrayList<Diff>> getDiffs() {
        return this.diffs;
    }

    public ArrayList<Diff> getDiff(String fileName) {
        return MapUtil.getObjectOr(this.diffs, fileName, new ArrayList<Diff>());
    }

    @Override
    public boolean compare() throws Exception {
        // Initial the comparison
        diffs = new HashMap();
        HashMap<String, File> expFiles = getSubFiles(this.expected);
        HashMap<String, File> actFiles = getSubFiles(this.actual);

        // Compare every file in the expected directory
        for (String fileName : expFiles.keySet()) {
            File expFile = expFiles.get(fileName);
            if (actFiles.containsKey(fileName)) {
                File actFile = actFiles.remove(fileName);
                FileComparator comparator = FileComparatorFactory.getFileComparator(expFile, actFile);
                if (!comparator.compare()) {
                    addDiffMsgArr(fileName, comparator.getDiff());
                }
            } else {
                addDiffMsg(fileName, new MissingFileDiff(Diff.TYPE.DELETE, fileName, true));
            }
        }

        // Check if there is any file remained in the actual result directory
        for (String fileName : actFiles.keySet()) {
            addDiffMsg(fileName, new MissingFileDiff(Diff.TYPE.INSERT, fileName, true));
        }

        return diffs.isEmpty();
    }

    protected HashMap<String, File> getSubFiles(File dir) {
        HashMap<String, File> ret = new HashMap();
        for (File f : dir.listFiles()) {
            ret.put(f.getName(), f);
        }
        return ret;
    }

    protected void addDiffMsg(String fileName, Diff diff) {
        ArrayList<Diff> msgs = this.diffs.get(fileName);
        if (msgs == null) {
            msgs = new ArrayList();
            this.diffs.put(fileName, msgs);
        }
        msgs.add(diff);
    }

    protected void addDiffMsgArr(String fileName, ArrayList<Diff> msgArr) {
        ArrayList<Diff> msgs = this.diffs.get(fileName);
        if (msgs == null) {
            this.diffs.put(fileName, msgArr);
        } else {
            msgs.addAll(msgArr);
        }
    }

    @Override
    public HashMap<File, File> getDiffFiles() {
        HashMap<File, File> files = new HashMap();
        for (String fileName : this.diffs.keySet()) {
            files.put(new File(actual.getPath() + File.separator + fileName),
                    new File(expected.getPath() + File.separator + fileName));
        }
        return files;
    }
}
