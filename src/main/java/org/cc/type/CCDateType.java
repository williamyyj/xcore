/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.type;

import org.cc.util.CCLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.cc.util.CCCast;


public class CCDateType extends CCBaseType<Date> {

    @Override
    public String dt() {
        return dt_date;
    }

    @Override
    public Date value(Object o, Date dv) {
        Date d = CCCast._date(o);
        return (d!=null) ? d : dv ; 
    }

    public Date check(Object o, String fmt) {
        if (o instanceof Date) {
            return ((Date) o);
        } else if (o instanceof String) {
            try {
                String text = o.toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                return sdf.parse(text);
            } catch (ParseException ex) {
                CCLogger.debug("Can't cast date : " + o);
            }
        }
        return null;
    }

    @Override
    public Date getRS(ResultSet rs, String name) throws SQLException {
        java.sql.Timestamp ts = rs.getTimestamp(name);
        if (ts != null) {
            return new Date(ts.getTime());
        }
        return null;
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        Date d = value(value);
        if (value == null) {
            ps.setNull(idx, Types.TIMESTAMP);
        } else {
            java.sql.Timestamp ts = new java.sql.Timestamp(d.getTime());
            ps.setTimestamp(idx, ts);
        }
    }

    @Override
    public Class<?> nativeClass() {
        return Date.class;
    }

    @Override
    public int jdbc() {
        return Types.TIMESTAMP;
    }



}
