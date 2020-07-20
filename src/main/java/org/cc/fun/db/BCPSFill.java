package org.cc.fun.db;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.function.BiConsumer;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.util.CCJSON;
import org.cc.util.CCLogger;


/**
 *
 * @author william
 */
public class BCPSFill implements BiConsumer<PreparedStatement, Object[]> {

    private volatile boolean pmdKnownBroken = false;

    @Override
    public void accept(PreparedStatement ps, Object[] params) {
        // nothing to do here
        ParameterMetaData pmd = null;
        try {           
            if (params == null) {
                return;
            }
            if (!pmdKnownBroken) {
                pmd = ps.getParameterMetaData();
                int stmtCount = pmd.getParameterCount();
                int paramsCount = params.length;
                if (stmtCount != paramsCount) {
                    CCLogger.error("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
                    return;
                }
            }
            
            for (int i = 0; i < params.length; i++) {
                set_param(pmd, ps, i, params[i]);
            }
        } catch (Exception e) {
            CCLogger.error(e);
        }

    }

    private void set_param(ParameterMetaData pmd, PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value != null) {
            if (value instanceof Date) {
                ps.setTimestamp(idx + 1, new Timestamp(((Date) value).getTime()));
            } else if (value instanceof ICCList) {
                value = CCJSON.toString((ICCList) value);
                ps.setObject(idx + 1, value);
            } else if (value instanceof ICCMap) {
                value = CCJSON.toString((ICCMap) value);
                ps.setObject(idx + 1, value);
            } else {
                ps.setObject(idx + 1, value);
            }
        } else {
            int sqlType = Types.VARCHAR;
            if (!pmdKnownBroken) {
                try {
                    sqlType = pmd.getParameterType(idx + 1);
                } catch (SQLException e) {
                    pmdKnownBroken = true;
                }
            }
            ps.setNull(idx+ 1, sqlType);
        }
    }
}
