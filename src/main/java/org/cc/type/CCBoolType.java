/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author william
 */
public class CCBoolType extends CCBaseType<Boolean> {

    public String dt() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boolean value(Object o, Boolean dv) {
        if (o instanceof Boolean) {
            return ((Boolean) o);
        } else if (o instanceof String) {
            String str = ((String) o).trim();
            return ("true".equalsIgnoreCase(str) || "Y".equalsIgnoreCase(str)
                    || "1".equalsIgnoreCase(str));
        } else if (o instanceof Number) {
            return (((Number) o).intValue() == 1);
        }
        return dv;
    }

    public Boolean getRS(ResultSet rs, String name) throws SQLException {
        return value(rs.getObject(name));
    }

    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        // 這比較麻煩要再想一想
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class<?> nativeClass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int jdbc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
