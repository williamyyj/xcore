package org.cc.type;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: william Date: 2013/7/29 Time: 下午 5:21 To
 * change this template use File | Settings | File Templates.
 */
public class ms_date extends CCDateType {

    @Override
    public String sql_value(Object value) {
        SimpleDateFormat sdf = new SimpleDateFormat(dslfmt);
        //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        if (value instanceof String) {
            String ret = ((String) value).replaceAll("'", "''");
            return "'" + ret + "'";
        } else if (value instanceof Date) {
            return "'" + sdf.format((Date) value) + "'";
        } else {
            return "NULL";
        }
    }

}
