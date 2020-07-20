package org.cc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author william
 */
public class CCCast {

    public static String sfmt = "yyyyMMdd";
    public static String lfmt = "yyyyMMddHHmmss";
    public static String afmt = "yyyyMMddHHmmssSSS";
    public static String cstfmt = "EEE MMM dd HH:mm:ss zzz yyyy";
    
    

    public static int _int(Object o, int dv) {
        try {
            if (o instanceof Number) {
                return ((Number) o).intValue();
            } else if (o instanceof String) {
                String str = ((String) o).trim();
                return str.length() > 0 ? Integer.parseInt(str) : dv;
            }
        } catch (Exception e) {
            CCLogger.warn("Can't cast " + o);
        }
        return dv;
    }

    public static long _long(Object o, long dv) {
        try {
            if (o instanceof Number) {
                return ((Number) o).longValue();
            } else if (o instanceof String) {
                String str = ((String) o).trim();
                return str.length() > 0 ? Long.parseLong(str) : dv;
            }
        } catch (Exception e) {
            CCLogger.warn("Can't cast " + o);
        }
        return dv;
    }

    public static double _double(Object o, double dv) {
        try {
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            } else if (o instanceof String) {
                String str = ((String) o).trim();
                return str.length() > 0 ? Double.parseDouble(str) : dv;
            }
        } catch (Exception e) {
            CCLogger.warn("Can't cast " + o);
        }
        return dv;
    }

    public static boolean _bool(Object o, boolean dv) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof String) {
            return Boolean.parseBoolean(((String) o).trim());
        } else {
            return dv;
        }
    }

    public static String _string(Object o, String dv) {
        return (o != null) ? o.toString().trim() : dv;
    }

    public static String _string(Object o) {
        return _string(o, "");
    }

    public static Date _date(Object o) {
        if (o instanceof Date) {
            return (Date) o;
        } else if (o instanceof String) {
            return to_date((String) o);
        }
        return null;
    }

    public static Date to_date(String text) {
        if (text.contains("CST ")) {
            return to_cst(text);
        }
        String str = text.replaceAll("[^0-9\\.]+", "");
        int len = str.length();
        switch (len) {
            case 8:
                return to_date(sfmt, str);
            case 14:
                return to_date(lfmt, str);
        }
        return null;
    }

    public static Date to_date(String fmt, String text) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            return sdf.parse(text);
        } catch (ParseException ex) {
            CCLogger.warn("Can't cast " + fmt+","+text);
        }
        return null;
    }

    public static Date to_cst(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat(cstfmt, Locale.US);
        try {
            return sdf.parse(text);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
}
