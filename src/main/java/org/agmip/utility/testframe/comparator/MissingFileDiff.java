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
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        if (isActualMissing) {
            sb.append(String.format("%s is expected but missing in the result of current run.<br />", fileName));
        } else {
            sb.append(String.format("%s is unexpected and existed in the result of current run.<br />", fileName));
        }
        return sb.toString();
    }
}
