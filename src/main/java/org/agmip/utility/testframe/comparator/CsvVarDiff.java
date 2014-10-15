package org.agmip.utility.testframe.comparator;

import java.util.ArrayList;
import java.util.HashMap;
import org.agmip.util.MapUtil;

/**
 *
 * @author Meng Zhang
 */
public class CsvVarDiff extends Diff {

    private final HashMap<String, String> expData;
    private final HashMap<String, String> actData;
    private final String keyName;
    private final String keyValue;
    private final ArrayList<String> headers;

    public CsvVarDiff(TYPE type, String keyName, String keyValue, HashMap<String, String> expData, HashMap<String, String> actData, ArrayList<String> headers) {
        super(type);
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.expData = expData;
        this.actData = actData;
        this.headers = headers;
    }

    @Override
    public String getHtmlString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append(String.format("%s &lt;%s&gt; :&nbsp;%s<br />", keyName, keyValue, super.getHtmlString()));
        sb.append("<table cellpadding=\"2\" class=\"stdTable\">");
        // header line
        sb.append("<tr class=\"stdTableHeader\"><td class=\"stdTableCell\"></td>");
        for (String header : headers) {
            sb.append(String.format("<td class=\"stdTableCell\">%s</td>", header));
        }
        sb.append("</tr>");
        // data line
        sb.append("<tr><td class=\"stdTableCell\">Expected</td>");
        for (String header : headers) {
            String expVal = MapUtil.getValueOr(expData, header, "");
            String actVal = MapUtil.getValueOr(actData, header, "");
            if (expVal.equals(actVal)) {
                sb.append(String.format("<td class=\"stdTableCell\">%s</td>", expVal));
                sb2.append(String.format("<td class=\"stdTableCell\">%s</td>", actVal));
            } else {
                sb.append(String.format("<td class=\"stdTableDiffCell\">%s</td>", expVal));
                sb2.append(String.format("<td class=\"stdTableDiffCell\">%s</td>", actVal));
            }
        }
        sb.append("</tr><tr><td class=\"stdTableCell\">Actual</td>");
        sb.append(sb2.toString());
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }
}
