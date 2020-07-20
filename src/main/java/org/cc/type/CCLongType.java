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
import org.cc.util.CCCast;

/**
 *
 * @author william
 */
public class CCLongType extends CCBaseType<Long> {

    @Override
    public String dt() {
        return dt_long;
    }

    @Override
    public Long value(Object o) {
        return value(o, 0L);
    }

    @Override
    public Long value(Object o, Long dv) {
        return CCCast._long(o, dv);
    }

    @Override
    public Long getRS(ResultSet rs, String name) throws SQLException {
        return rs.getLong(name);
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, Types.DECIMAL);
        } else {
            ps.setLong(idx, value(value, 0L));
        }
    }

    @Override
    public Class<?> nativeClass() {
        return Long.TYPE;
    }

    @Override
    public int jdbc() {
        return Types.DECIMAL;
    }

}
