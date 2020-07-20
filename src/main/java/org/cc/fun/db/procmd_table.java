package org.cc.fun.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author William
 */
public class procmd_table implements Function<Object[], List<String>> {

    @Override
    public List<String> apply(Object[] args) {
        DatabaseMetaData dbmd = (DatabaseMetaData) args[0];
        String catalog = (String) args[1];
        String schema = (String) args[2];
        return exec(dbmd, catalog, schema);
    }

    public List<String> exec(DatabaseMetaData dbmd, String catalog, String schema) {
        String[] DBTypes = {"TABLE"};
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = dbmd.getTables(catalog, schema, null, DBTypes);
            while (rs.next()) {
                String tbName = rs.getString("TABLE_NAME");
                // FIX oracle 
                if (!tbName.startsWith("BIN$")) {
                    list.add(tbName);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {

                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
