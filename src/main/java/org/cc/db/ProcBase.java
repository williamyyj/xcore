package org.cc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.ICCType;
import org.cc.db.IDB;
import org.cc.fun.db.BCPSFill;
import org.cc.fun.db.BFRS2Row;
import org.cc.fun.db.BFRS2Rows;
import org.cc.fun.db.BFRSMetadata;
import org.cc.type.CCTypes;
import org.cc.util.CCLogger;

/**
 *
 * @author william
 */
public class ProcBase {

    protected BiConsumer<PreparedStatement, Object[]> ps_fill = new BCPSFill();
    protected BiFunction<ICCList, ResultSet, ICCMap> rs_row = new BFRS2Row();
    protected BiFunction<ICCList, ResultSet, ICCList> rs_rows = new BFRS2Rows();
    protected BiFunction<CCTypes, ResultSet, ICCList> rs_metadata = new BFRSMetadata();

    protected void proc_fill(IDB db, PreparedStatement ps, ICCList fields) throws SQLException {
        int len = fields.size();
        for (int i = 0; i < len; i++) {
            ICCMap fld = fields.map(i);
            //System.out.println(fld);
            String dt = fld.asString("dt");
            Object value = fld.get("value");
            ICCType<?> type = db.types().type(dt);
            type.setPS(ps, i + 1, value);
        }
    }
    
    


    public void __release(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                CCLogger.error(ex);
            }
        }
    }

    public void __release(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                CCLogger.error(ex);
            }
        }
    }

    public void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                CCLogger.error(ex);
            }
        }
    }

}
