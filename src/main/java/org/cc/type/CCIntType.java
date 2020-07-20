package org.cc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.cc.util.CCCast;

/**
 * @author williamyyj
 */
public class CCIntType extends CCBaseType<Integer> {

    @Override
    public String dt() {
        return dt_int;
    }

    @Override
    public Integer value(Object o) {
        return value(o, 0);
    }

    @Override
    public Integer value(Object o, Integer dv) {
        return CCCast._int(o, dv);
    }

    @Override
    public Integer getRS(ResultSet rs, String name) throws SQLException {
        return rs.getInt(name);
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value == null) {
            ps.setNull(idx, Types.INTEGER);
        } else {
            ps.setInt(idx, value(value, 0));
        }
    }

    @Override
    public Class<?> nativeClass() {
        return Integer.TYPE;
    }

    @Override
    public int jdbc() {
        return Types.INTEGER;
    }
}
