package org.agmip.utility.testframe.comparator;

/**
 *
 * @author Meng Zhang
 */
public class TextFileDiff extends Diff {
    private final int lineNum ;
    private final String expLine;
    private final String actLine;
    
    public TextFileDiff(TYPE type, int lineNum, String expLine, String actLine) {
        super(type);
        this.lineNum = lineNum;
        this.expLine = expLine;
        this.actLine = actLine;
    }
    
    public int getLineNum() {
        return this.lineNum;
    }
    
    public String getExpLine() {
        return this.expLine;
    }
    
    public String getActLine() {
        return this.actLine;
    }
    
    @Override
    public String getHtmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("line %d:&nbsp;%s<br />", lineNum, super.getHtmlString()));
        sb.append(String.format("<strong>Expected</strong> : %s<br />", getExpLine()));
        sb.append(String.format("<strong>Actual&nbsp;&nbsp</strong> : %s<br />", getActLine()));
        return sb.toString();
    }
}
