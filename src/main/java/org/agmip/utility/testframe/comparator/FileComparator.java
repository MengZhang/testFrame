package org.agmip.utility.testframe.comparator;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.agmip.util.MapUtil;

/**
 *
 * @author Meng Zhang
 */
public class FileComparator extends TestComparator {

    protected FileComparator(File expected, File actual) throws Exception {
        super(expected, actual);
        if (!expected.isFile() && !expected.getName().matches(".+\\..+")) {
            throw new Exception("Expected result file is invalid [" + expected.getName() + "]");
        } else if (!actual.isFile() && !expected.getName().matches(".+\\..+")) {
            throw new Exception("Actual result file is invalid [" + actual.getName() + "]");
        }
        compareType = Type.FILE;
    }

    @Override
    public HashMap<String, ArrayList<Diff>> getDiffs() {
        return this.diffs;
    }

    public ArrayList<Diff> getDiff() {
        return MapUtil.getObjectOr(this.diffs, expected.getName(), new ArrayList<Diff>());
    }

    @Override
    public boolean compare() throws Exception {
        diffs = new HashMap();
        ArrayList<Diff> difference = new ArrayList();
        try {
            Patch patch = DiffUtils.diff(getFileContent(this.expected, false), getFileContent(this.actual, true));
            for (Delta delta : patch.getDeltas()) {

                Diff.TYPE type;
                int lineNum;
                List expLines = delta.getOriginal().getLines();
                List actLines = delta.getRevised().getLines();
                if (delta.getType().equals(Delta.TYPE.CHANGE)) {
                    lineNum = delta.getRevised().getPosition() + 1;
                    type = Diff.TYPE.CHANGE;
                } else if (delta.getType().equals(Delta.TYPE.DELETE)) {
                    lineNum = delta.getOriginal().getPosition() + 1;
                    type = Diff.TYPE.DELETE;
                } else {
                    lineNum = delta.getRevised().getPosition() + 1;
                    type = Diff.TYPE.INSERT;
                }
                if (!expLines.isEmpty() && (!actLines.isEmpty() || type.equals(Diff.TYPE.DELETE))) {
                    //                TextFileDiff
                    difference.add(
                            new TextFileDiff(type,
                                    lineNum,
                                    (String) expLines.get(0),
                                    (String) actLines.get(0))
                    );
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (!difference.isEmpty()) {
            diffs.put(expected.getName(), difference);
        }
        return difference.isEmpty();
    }

    protected ArrayList<String> getFileContent(File in, boolean isActual) throws IOException {
        try {
            ArrayList<String> ret = new ArrayList();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line);
            }
            br.close();
            return ret;
        } catch (IOException e) {
            ArrayList<Diff> difference = new ArrayList();
            if (isActual) {
                difference.add(new MissingFileDiff(Diff.TYPE.DELETE, actual.getName(), isActual));
            } else {
                difference.add(new MissingFileDiff(Diff.TYPE.INSERT, actual.getName(), isActual));
            }
            diffs.put(expected.getName(), difference);
            throw e;
        }
    }
}
