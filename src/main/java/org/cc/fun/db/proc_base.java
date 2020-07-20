package org.cc.fun.db;

import java.sql.DatabaseMetaData;
import org.cc.model.CCProcObject;



/**
 *
 * @author william
 */
public class proc_base {

    public DatabaseMetaData dbmd(CCProcObject proc) throws Exception {
        DatabaseMetaData dbmd = (DatabaseMetaData) proc.get("$dbmd");
        if (dbmd == null) {
            dbmd = proc.db().connection().getMetaData();
            proc.put("$dbmd", dbmd);
        }
        return dbmd;
    }

    public String catalog(CCProcObject proc) {
        return proc.params().asString("catalog", proc.db().catalog());
    }

    public String schema(CCProcObject proc) {
        return proc.params().asString("schema", proc.db().schema());
    }



}
