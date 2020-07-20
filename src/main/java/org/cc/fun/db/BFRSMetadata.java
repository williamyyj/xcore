package org.cc.fun.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.function.BiFunction;
import org.cc.CCList;
import org.cc.CCMap;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.ICCType;
import org.cc.type.CCTypes;
import org.cc.util.CCLogger;


/**
 *
 * @author william
 */
public class BFRSMetadata implements BiFunction<CCTypes, ResultSet, ICCList> {

    @Override
    public ICCList apply(CCTypes types, ResultSet rs) {
        ICCList metadata = new CCList();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int len = rsmd.getColumnCount();
            for (int i = 0; i < len; i++) {
                int idx = i + 1;
                ICCMap meta = new CCMap();
                String name = rsmd.getColumnName(idx);
                int dt = rsmd.getColumnType(idx);
                ICCType type = types.type(dt);
                meta.put("name", name);
                meta.put("dt", type.dt());
                meta.put("type", type);
                metadata.add(meta);
            }
        } catch (SQLException ex) {
            CCLogger.error(ex);
        }
        return metadata;
    }
}
