package org.cc.fun.dp;


import java.util.List;
import java.util.function.Function;
import org.cc.model.field.ICCField;

public class FSQLInsert extends FSQLBase implements  Function<List<ICCField>,String> {

    @Override
    public String apply(List<ICCField> fields)   {
        ICCField tb = fields.get(0);
        if (!"table".equals(tb.dt())) {
            throw new RuntimeException("JOField must tb field : " + tb);
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" insert into ").append(tb.name());
        proc_cols_name(sql, fields);
        sql.append("\r\n values ");
        proc_cols_value(sql, fields);
        proc_scope(sql,fields.get(1));
        return sql.toString();
    }

    private void proc_cols_name(StringBuilder sql, List<ICCField> fields) {
        sql.append(" (");
        for (ICCField field : fields) {
            if (!"table".equals(field.dt())  && !"auto".equals(field.cfg().asString("ft")) ) {
                sql.append(' ').append(field.name()).append(",");
            }
        }
        sql.setCharAt(sql.length() - 1, ')');
    }

    private void proc_cols_value(StringBuilder sql, List<ICCField> fields) {
        sql.append(" (");
        for (ICCField field : fields) {
            if (!"table".equals(field.dt()) && !"auto".equals(field.cfg().asString("ft")) ) {
                sql.append(" ${");
                sql.append(field.name()).append(',');
                sql.append(field.dt()).append(',');
                sql.append(field.id());
                sql.append("},");
            }
        }
        sql.setCharAt(sql.length() - 1, ')');
    }

    private void proc_scope(StringBuilder sql, ICCField fld) {
        String jdbc = fld.cfg().asString("jdbc");
        if(jdbc.contains("identity")){
            sql.append(";SELECT SCOPE_IDENTITY();");
        }
    }

}
