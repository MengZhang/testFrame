package org.agmip.utility.testframe.comparator;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class CsvFileComparator extends FileComparator {

    private HashMap<String, HashMap<String, String>> expDiffData;
    private HashMap<String, HashMap<String, String>> actDiffData;
    protected int headerRawNum = 0;
    protected String keyHeaderName = null;
    private int keyHeaderIdx = -1;
    protected ArrayList<String> compareHeaderNames = null;
    private LinkedHashMap<String, Integer> compareHeaderIdxs;
    protected String spliter = ",";
    private static final Logger LOG = LoggerFactory.getLogger(CsvFileComparator.class);

    protected CsvFileComparator(File expected, File actual) throws Exception {
        super(expected, actual);
        if (!expected.getName().toUpperCase().matches(".+\\.CSV")) {
            throw new Exception("Expected result CSV file is invalid [" + expected.getName() + "]");
        } else if (!expected.getName().toUpperCase().matches(".+\\.CSV")) {
            throw new Exception("Actual result CSV file is invalid [" + actual.getName() + "]");
        }
    }

    @Override
    public HashMap toMap() {
        HashMap ret = super.toMap();
        ret.put("headerRawNum", headerRawNum);
        ret.put("keyHeaderName", keyHeaderName);
        ret.put("compareHeaderNames", compareHeaderNames);
        ret.put("spliter", spliter);
        return ret;
    }

    public void setHeaderRawNum(int headerRawNum) {
        this.headerRawNum = headerRawNum;
    }

    public void setKeyHeaderName(String keyHeaderName) {
        this.keyHeaderName = keyHeaderName;
    }

    public void setCompareAllCols() {
        this.compareHeaderNames = null;
    }

    public void setCompareHeaderNames(ArrayList<String> compareHeaderNames) {
        this.compareHeaderNames = compareHeaderNames;
    }

    public void addCompareCol(String compareHeaderName) {
        if (this.compareHeaderNames == null) {
            this.compareHeaderNames = new ArrayList();
        }
        this.compareHeaderNames.add(compareHeaderName);
    }

    public void setSpliter(String spliter) {
        this.spliter = spliter;
    }

    @Override
    public boolean compare() throws Exception {
        this.expDiffData = new HashMap();
        this.actDiffData = new HashMap();
        boolean ret = super.compare();
        if (!ret) {
            ArrayList<Diff> textDiffs = new ArrayList(this.getDiff());
            this.getDiff().clear();
            if (!readCsvFileHeaderIdx()) {
                return ret;
            }
            for (Diff diff : textDiffs) {
                if (diff instanceof TextFileDiff) {
                    TextFileDiff tDiff = ((TextFileDiff) diff);
                    int rawNum = tDiff.getLineNum();
                    if (rawNum < headerRawNum) {
                        continue;
                    }
                    HashMap<String, String> expData = new HashMap();
                    String expKey = readLine(tDiff.getExpLine(), expData);
                    HashMap<String, String> actData = new HashMap();
                    String actKey = readLine(tDiff.getActLine(), actData);
                    if (expKey == null || actKey == null) {
                        if (!actData.equals(expData)) {
                            this.getDiff().add(new CsvVarDiff(Diff.TYPE.CHANGE, "line", rawNum + "", expData, actData, compareHeaderNames));
                        }
                    } else {
                        if (actKey.equals(expKey)) {
                            if (!actData.equals(expData)) {
                                this.getDiff().add(new CsvVarDiff(Diff.TYPE.CHANGE, keyHeaderName, expKey, expData, actData, compareHeaderNames));
                            }
                        } else {
                            if (this.actDiffData.containsKey(expKey)) {
                                HashMap<String, String> tmp = actDiffData.remove(expKey);
                                if (!tmp.equals(expData)) {
                                    this.getDiff().add(new CsvVarDiff(Diff.TYPE.CHANGE, keyHeaderName, expKey, expData, tmp, compareHeaderNames));
                                }
                            } else {
                                this.expDiffData.put(expKey, expData);
                            }
                            if (this.expDiffData.containsKey(actKey)) {
                                HashMap<String, String> tmp = expDiffData.remove(actKey);
                                if (!tmp.equals(actData)) {
                                    this.getDiff().add(new CsvVarDiff(Diff.TYPE.CHANGE, keyHeaderName, expKey, tmp, actData, compareHeaderNames));
                                }
                            } else {
                                this.actDiffData.put(expKey, expData);
                            }
                        }
                    }
                } else {
                    this.getDiff().add(diff);
                }
                if (!expDiffData.isEmpty()) {
                    for (String key : expDiffData.keySet()) {
                        this.getDiff().add(new CsvVarDiff(Diff.TYPE.DELETE, keyHeaderName, key, expDiffData.get(key), new HashMap<String, String>(), compareHeaderNames));
                    }
                }
                if (!actDiffData.isEmpty()) {
                    for (String key : actDiffData.keySet()) {
                        this.getDiff().add(new CsvVarDiff(Diff.TYPE.DELETE, keyHeaderName, key, new HashMap<String, String>(), actDiffData.get(key), compareHeaderNames));
                    }
                }
            }
        }
        return ret;
    }

    private boolean readCsvFileHeaderIdx() throws IOException {
        keyHeaderIdx = -1;
        compareHeaderIdxs = new LinkedHashMap();
        ArrayList<String> expHeaders = readCsvFileHeader(expected);
        ArrayList<String> actHeaders = readCsvFileHeader(actual);
        if (!actHeaders.equals(expHeaders)) {
            this.getDiff().add(new CsvHeaderDiff(expHeaders, actHeaders));
            return false;
        }
        if (keyHeaderName != null) {
            keyHeaderIdx = expHeaders.indexOf(keyHeaderName);
            if (keyHeaderIdx < 0) {
                LOG.warn("Key header {} is missing in {}", keyHeaderName, expected.getName());
            }
        }
        if (compareHeaderNames == null) {
            compareHeaderNames = new ArrayList();
            compareHeaderNames.addAll(expHeaders);
        }
        for (String compareheaderName : compareHeaderNames) {
            int expIdx = expHeaders.indexOf(compareheaderName);
            if (expIdx < 0) {
                LOG.warn("Header {} is missing in expected {}", compareheaderName, expected.getName());
            }
            compareHeaderIdxs.put(compareheaderName, expIdx);
        }

        return true;
    }

    private ArrayList<String> readCsvFileHeader(File f) throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(f)), spliter.charAt(0));
        int nextRawNum = 0;
        while (nextRawNum < headerRawNum) {
            reader.readNext();
            nextRawNum++;
        }
        String[] line = reader.readNext();
        reader.close();
        if (line != null) {
            return new ArrayList(Arrays.asList(line));
        } else {
            LOG.warn("Fail to read header for {}", f.getName());
            return new ArrayList();
        }
    }

    private String readLine(String lineStr, HashMap<String, String> data) throws IOException {

        CSVReader reader = new CSVReader(new StringReader(lineStr), spliter.charAt(0));
        String[] line = reader.readNext();
        reader.close();
        if (line != null) {
            for (String headerName : compareHeaderNames) {
                int idx = compareHeaderIdxs.get(headerName);
                if (idx > -1 && idx < line.length) {
                    data.put(headerName, line[idx]);
                } else {
                    data.put(headerName, "");
                }
            }
            if (keyHeaderIdx > -1 && keyHeaderIdx < line.length) {
                return line[keyHeaderIdx];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
