package org.cc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CCClobType extends CCBaseType<String> {

    @Override
    public String dt() {
        return dt_clob;
    }

    @Override
    public String value(Object o, String dv) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getRS(ResultSet rs, String name) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class<?> nativeClass() {
        return String.class;
    }

    public int jdbc() {
        return Types.CLOB;
    }

    
    
}
