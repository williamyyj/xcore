/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.fun.proc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiFunction;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.db.DBCmdPS;
import org.cc.db.ProcBase;

import org.cc.model.CCProcObject;
import org.cc.model.field.CCField;

/**
 *
 * @author william
 */
public class FDBBatchUpdate extends ProcBase implements BiFunction<CCProcObject, DBCmdPS, ICCList> {

    @Override
    public ICCList apply(CCProcObject proc, DBCmdPS cmd) {
        PreparedStatement ps = null;

        Connection conn = null;
        boolean flag = false;
        try {
            conn = proc.db().connection();
            conn.setAutoCommit(false);
            ps = proc.db().connection().prepareStatement(cmd.pCmd());
            ICCList data = proc.list("$data");
            if (data != null) {
                int idx = 0;
                int count = 0;
                for (Object o : data) {
                    ICCMap row = (ICCMap) o;
                    proc_add_batch(ps, cmd, row);
                    idx++;
                    if (idx % 1000 == 0) {
                        System.out.println("===== proc count :" + count);
                        ps.executeBatch();
                        conn.commit();
                        idx = 0;
                    }
                    count++;
                }
                if (idx > 0) {
                    ps.executeBatch();
                    conn.commit();
                }

            }
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            rollback(conn);
        } finally {
            __release(ps);
        }
        return null;
    }

    private void proc_add_batch(PreparedStatement ps, DBCmdPS cmd, ICCMap row) throws SQLException {
        for (int i = 0; i < cmd.pFields().size(); i++) {
            CCField fld = cmd.pFields().get(i);
            Object v = fld.getFieldValue(row);
            fld.type().setPS(ps, i + 1, v);
        }
        ps.addBatch();
    }

}
