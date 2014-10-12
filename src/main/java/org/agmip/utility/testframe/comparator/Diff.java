package org.agmip.utility.testframe.comparator;

/**
 *
 * @author Meng Zhang
 */
public abstract class Diff {
    
    public enum TYPE { CHANGE, DELETE, INSERT }
    
    private final TYPE type;
    
    public Diff(TYPE type) {
        this.type = type;
    }
    
    public TYPE getType() {
        return this.type;
    }
    
    public String getHtmlString() {
        return String.format("<span %s><strong>%s</strong></span>", getStyle(), this.type);
    }
    
    private String getStyle() {
        if (this.type.equals(TYPE.CHANGE)) {
            return "style=\"color: #FF0000\"";
        } else if (this.type.equals(TYPE.INSERT)) {
            return "style=\"color: #0000FF\"";
        } else {
            return "";
        }
    }
}
