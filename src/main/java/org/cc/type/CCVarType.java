/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.type;

import org.cc.ICCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class CCVarType extends CCBaseType<Object> {
    
    private int dt_sql ;
    
    public CCVarType(int dt_sql) {
        this.dt_sql = dt_sql;
    }
    
    public CCVarType(){
        
    }
    
    public String dt() {
        return ICCType.dt_var;
    }

    public Object value(Object o, Object dv) {
        return (o!=null) ? o : dv;
    }

    public Object getRS(ResultSet rs, String name) throws SQLException {
        return rs.getObject(name);
    }

    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        if(value==null){
            ps.setNull(idx, Types.VARCHAR);
        } else {
            ps.setObject(idx, value);
        }
    }

    public Class<?> nativeClass() {
        return Object.class;
    }
    
    public int jdbc(){
        return dt_sql;
    }
    
}
