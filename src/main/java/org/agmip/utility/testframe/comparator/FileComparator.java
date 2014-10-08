package org.agmip.utility.testframe.comparator;

import difflib.Chunk;
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
    public HashMap<String, ArrayList<String>> getDiff() {
        return this.diff;
    }
    
    public ArrayList<String> getSingleDiff() {
        return this.diff.get(this.title);
    }

    @Override
    public boolean compare() throws Exception {
        diff = new HashMap();
        ArrayList<String> difference = new ArrayList();
        Patch patch = DiffUtils.diff(getFileContent(this.expected), getFileContent(this.actual));
        for (Delta delta : patch.getDeltas()) {

            Chunk chunk;
            if (delta.getType().equals(Delta.TYPE.CHANGE)) {
                chunk = delta.getRevised();
            } else if (delta.getType().equals(Delta.TYPE.DELETE)) {
                chunk = delta.getOriginal();
            } else {
                chunk = delta.getRevised();
            }
            List changes = chunk.getLines();
            if (!changes.isEmpty()) {
                difference.add(String.format("[%s][line %d][%s]", delta.getType().toString(), chunk.getPosition() + 1, changes.get(0).toString()));
            }
        }
        diff.put(this.title, difference);

        return difference.isEmpty();
    }

    protected ArrayList<String> getFileContent(File in) throws IOException {
        ArrayList<String> ret = new ArrayList();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
        String line;
        while ((line = br.readLine()) != null) {
            ret.add(line);
        }
        return ret;
    }
}
