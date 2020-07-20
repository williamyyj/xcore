package org.cc.fun.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.cc.model.CCProcObject;

/**
 * @author william
 */
public class procmd_pk extends proc_base implements Function<CCProcObject, Set<String>> {

    @Override
    public Set<String> apply(CCProcObject proc) {
        String catalog = proc.params().asString("catalog", proc.db().catalog());
        String schema = proc.params().asString("schema", proc.db().schema());
        String table = proc.params().asString("table");
        return exec(proc, catalog, schema, table);
    }

    public Set<String> exec(CCProcObject proc, String catalog, String schema, String table) {
        Set<String> list = new HashSet<String>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = dbmd(proc);
            rs = dbmd.getPrimaryKeys(catalog, schema, table);
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                list.add(name);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
