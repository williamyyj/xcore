package org.cc.fun.dp;

import java.util.List;
import java.util.function.Function;
import org.cc.model.field.ICCField;


/**
 *   
 * @author william
 */
public class FSQLUpdate extends FSQLBase implements Function<List<ICCField>, String> {

    @Override
    public String apply(List<ICCField> fields) {
        ICCField tb = fields.get(0);
        if (!"table".equals(tb.dt())) {
            throw new RuntimeException("JOField must tb field : " + tb);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(tb.name()).append(" set");
        proc_cols_name(sql, fields);
        sql.append("\nwhere 1=1 ");
        proc_cols_cond(sql, fields);
        return sql.toString();
    }

    /*
     ${$set,mem_passwd,string,mempasswd}
     */
    private void proc_cols_name(StringBuilder sql, List<ICCField> fields) {
        for (ICCField fld : fields) {
            if (!"P".equals(fld.ct()) && !"table".equals(fld.dt())) {
                sql.append('\n').append("${$set")
                  .append(',').append(fld.name())
                  .append(',').append(fld.dt())
                  .append(',').append(fld.id())
                  .append('}');
            }
        }
        sql.append("${$rm}");
    }

    private void proc_cols_cond(StringBuilder sql, List<ICCField> fields) {
        for (ICCField fld : fields) {
            if ("P".equals(fld.ct()) || "U".equals(fld.ct())) {
                sql.append("${=,").append(fld.name())
                  .append(',').append(fld.dt())
                  .append(',').append(fld.id())
                  .append("}");
            }
        }

    }

}
