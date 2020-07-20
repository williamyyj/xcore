package org.cc.util;

import java.io.File;
import org.cc.CCList;
import org.cc.CCMap;
import org.cc.ICCList;
import org.cc.ICCMap;

public class CCJsonParser extends CCBuffer {

    private static final long serialVersionUID = 8384119638487566762L;

    public static final char QUOT = '"';
    /**
     * The pattern *
     */
    public final static String idPattern = " :()[]{}\\\"'";
    public final static String valuePattern = ",)]}>";
    public final static String wordPattern = " ,)]}>";
    public final static String opPattern = " ,)]}$";
    public final static String tk_lstr_start = "$\"";  // $".....""..."

    public CCJsonParser(File f, String enc) {
        super(f, enc);
    }

    public CCJsonParser(String text) {
        super(text);
    }

    public ICCMap parser_obj() {
        try {
            return cc_obj();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ICCList parser_list() {

        try {
            return cc_list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean tk_m(char c) {
        if (data.length > ps && data[ps] == c) {
            move(1);
            return true ;
        }
        return false;
    }

    public ICCMap cc_obj() throws Exception {
        if (!tk_m('{')) {
            throw error("Parser  exception '{' ");
        }
        ICCMap xo = new CCMap();
        for (;;) {
            tk_csp();
            if (tk_m('}')) {
                return xo;
            }
            String key = cc_next(idPattern).toString();
            tk_csp();
            if (!tk_m(':')) {
                throw error("Parser  expected ':' ");
            }
            tk_csp();
            xo.put(key, cc_next(valuePattern));
            tk_csp();
            if (tk_m('}')) {
                return xo;
            }
            if (!tk_m(',')) {
                throw error("Parser  expected ( ',' | '}' ) ");
            }
        }
    }

    protected ICCList cc_list() throws Exception {
        if (!tk_m('[')) {
            throw error("ICCList expected '['");
        }
        ICCList xa = new CCList();
        for (;;) {
            tk_csp();
            if (tk_m(']')) {
                return xa;
            }
            xa.add(cc_next(valuePattern));
            tk_csp();
            if (tk_m(']')) {
                return xa;
            }
            if (!tk_m(',')) {
                throw error("ICCList expected  (','|']') ");
            }
        }
    }

    public Object cc_next(String pattern) throws Exception {
        if (m('\'') || m('"')) {
            return cc_string(data[ps]);
        } else if (m('{')) {
            return cc_obj();
        } else if (m('[')) {
            return cc_list();
        }
        return cc_value(cc_word(pattern));
    }

    public String cc_word(String pattern) {
        int start = ps;
        // 合理換行是區原素 
        while (data[ps] >= 32 && !in(0, pattern)) {
            next();
        }
        return subString(start, ps).trim();
    }

    public Object cc_value(String s) {
        if (s.equals("")) {
            return s;
        }
        if (s.equalsIgnoreCase("true")) {
            return true;
        }
        if (s.equalsIgnoreCase("false")) {
            return false;
        }
        if (s.equalsIgnoreCase("null")) {
            return null;
        }

        char b = s.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0' && s.length() > 2
              && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
                try {
                    return Integer.parseInt(s.substring(2), 16);
                } catch (Exception ignore) {
                }
            }
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1
                  || s.indexOf('E') > -1) {
                    return Double.parseDouble(s);
                } else {
                    Long myLong = new Long(s);
                    if (myLong.longValue() == myLong.intValue()) {
                        return myLong.intValue();
                    } else {
                        return myLong.longValue();
                    }
                }
            } catch (Exception ignore) {
                //ignore.printStackTrace();
            }
        }
        return s;
    }

    public String cc_string(char quote) {
        return tk_string(quote);
    }

    public Exception error(String error) {
        String fmt = "Error (%s)%s in line:%s pos:%s ";
        String message = String.format(fmt,src, error, line, pos);
        CCLogger.debug(message);
        return new Exception(message);
    }

}
