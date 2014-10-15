package org.agmip.utility.testframe.comparator;

import java.util.ArrayList;

/**
 *
 * @author Meng Zhang
 */
public class CsvHeaderDiff extends Diff {
    
    private final ArrayList<String> expHeaders;
    private final ArrayList<String> actHeaders;
    
    public CsvHeaderDiff(ArrayList<String> expHeaders, ArrayList<String> actHeaders) {
        super(TYPE.CHANGE);
        this.expHeaders = expHeaders;
        this.actHeaders = actHeaders;
    }
    
    @Override
    public String getHtmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Header Line:&nbsp;%s<br />", super.getHtmlString()));
        sb.append(String.format("<strong>Expected headers</strong> : %s<br />", expHeaders));
        sb.append(String.format("<strong>Actual headers&nbsp;&nbsp;</strong> : %s<br />", actHeaders));
        return sb.toString();
    }
}
