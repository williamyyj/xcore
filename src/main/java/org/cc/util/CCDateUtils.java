package org.cc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author william
 */
public class CCDateUtils {

    public static Date newInstance(int year, int month, int date) {
        return newInstance(year, month, date, 0, 0, 0);
    }

    public static Date newInstance(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal.getTime();
    }

    public static Date toDate(Object fv) {
        if (fv instanceof Date) {
            return (Date) fv;
        } else if (fv instanceof String) {
            return to_date((String) fv);
        } else if (fv instanceof Number) {
            return new Date(((Number) fv).longValue());
        }
        return null;
    }

    public static Date to_date(String text) {
        try {
            String sfmt = "yyyyMMdd";
            String lfmt = "yyyyMMddHHmmss";
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            if (text.contains("CST ")) {
                return sdf.parse(text);
            }

            String str = text.replaceAll("[^0-9\\.]+", "");
            int len = str.length();
            switch (len) {
                case 7:
                    return cdate(str);
                case 8:
                    return to_date(sfmt, str);
                case 14:
                    return to_date(lfmt, str);
                default:
                    if (str.length() > 14) {
                        return to_date(lfmt, str.substring(0, 14));
                    }
                    return null;
            }
        } catch (ParseException ex) {
            //JOLogger.info("Can't parser Date : "+text);
        }
        return null;
    }

    public static Date cdate(Object text) {
        try {
            NumberFormat nf = new DecimalFormat("0000000");
            int dv = nf.parse(text.toString()).intValue();
            int year = (dv / 10000 + 1911) * 10000 + (dv % 10000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            return sdf.parse(String.valueOf(year));
        } catch (Exception ex) {
            //JOLogger.debug("Can't convet cdate : " + text);
            return null;
        }

    }

    public static Date to_date(String fmt, String text) {
        try {
            return new SimpleDateFormat(fmt).parse(text);
        } catch (ParseException ex) {
            //JOLogger.info("Can't parser date : " + text);
        }
        return null;
    }

    public static Date add(Date d, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(field, amount);
        return c.getTime();
    }

    public static Date addDate(Date d, int amount) {
        return add(d, Calendar.DATE, amount);
    }

}
