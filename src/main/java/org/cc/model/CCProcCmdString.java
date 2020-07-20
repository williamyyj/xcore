package org.cc.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  inParam >funId@metaId  >outParam   
 * @author william
 */
public class CCProcCmdString {

    private Pattern p = Pattern.compile("([a-zA-Z0-9_\\.]+)\\@(.+)");
    private String inParam;
    private String outParam;
    private String funId;
    private String params;

    public CCProcCmdString(String cmdString) {
        init(cmdString);
    }

    protected void init(String cmdString) {
        String[] items = cmdString.split(">");
        inParam = "$";
        outParam = "$data";
        switch (items.length) {
            case 1:
                init_fun(items[0]);
                break;
            case 2:
                if (items[0].contains("@")) {
                    init_fun(items[0]);
                    outParam = items[1];
                } else if (items[1].contains("@")) {
                    inParam = items[0];
                    init_fun(items[1]);
                }
                break;
            case 3:
                inParam = items[0];
                outParam = items[2];
                init_fun(items[1]);
                break;
        }
    }

    private void init_fun(String funPart) {
        Matcher m = p.matcher(funPart);
        if (m.find()) {
            funId = m.group(1);
            params = m.group(2);
        } else {
            funId = funPart;
            params = null;
        }

    }

    public String inParam() {
        return this.inParam;
    }

    public String outParam() {
        return this.outParam;
    }

    public String funId() {
        return funId;
    }

    public String params() {
        return params;
    }

    @Override
    public String toString() {
        return inParam + ">" + funId + "@" + params + ">" + outParam;
    }

}
