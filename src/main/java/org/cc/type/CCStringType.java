/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author william
 */

public class CCStringType extends CCBaseType<String> {

    @Override
    public String dt() {
        return dt_string;
    }

    @Override
    public String value(Object o, String dv) {
        return (o!=null) ? o.toString() : dv;
    }

    @Override
    public String getRS(ResultSet rs, String name) throws SQLException {
        String ret = rs.getString(name);
        return (ret!=null) ? ret.trim() : ret ; 
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
       if(value==null){
            ps.setNull(idx, Types.VARCHAR);
        } else {
            ps.setString(idx, value.toString());
        }
    }

    @Override
    public Class<?> nativeClass() {
        return String.class;
    }

    @Override
    public int jdbc() {
        return Types.VARCHAR;
    }


    @Override
    public String sql_value(Object value) {
        if(value!=null && !"null".equalsIgnoreCase((value.toString()))){
            String ret = value.toString();
            ret = ret.replaceAll("'","''");
            return "'" + ret + "'";

        }
        return "NULL";
    }


}
