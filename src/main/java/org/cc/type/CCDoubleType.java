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

public class CCDoubleType extends CCBaseType<Double> {

    @Override
    public String dt() {
        return dt_double;
    }

    @Override
    public Double value(Object o) {
        return value(o, 0.0);
    }

    @Override
    public Double value(Object o, Double dv) {
        return CCCast._double(o, dv);
    }

    @Override
    public Double getRS(ResultSet rs, String name) throws SQLException {
        return rs.getDouble(name);
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, Types.DOUBLE);
        } else {
            ps.setDouble(idx, value(value, 0.0));
        }
    }

    @Override
    public Class<?> nativeClass() {
        return Double.TYPE;
    }

    @Override
    public int jdbc() {
        return Types.DOUBLE;
    }

}
