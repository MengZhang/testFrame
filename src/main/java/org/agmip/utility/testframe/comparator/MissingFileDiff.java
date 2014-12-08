package org.agmip.utility.testframe.comparator;

/**
 *
 * @author Meng Zhang
 */
public class MissingFileDiff extends Diff {
    
    private final String fileName;
    private final boolean isActualMissing;
    
    public MissingFileDiff(TYPE type, String fileName, boolean isActualMissing) {
        super(type);
        this.fileName = fileName;
        this.isActualMissing = isActualMissing;
    }
    
    @Override
    public String getHtmlString() {
        if (isActualMissing) {
            return String.format("%s is expected but missing in the result of current run.<br />", fileName);
        } else {
            return String.format("%s is unexpected and existed in the result of current run.<br />", fileName);
        }
    }
}
