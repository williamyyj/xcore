package org.cc.fun.db;

import org.cc.db.ProcBase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Function;
import org.cc.CCConst;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.db.IDB;

/**
 *
 * @author william
 */
public class proc_row extends ProcBase implements Function<CCProcObject, ICCMap> {

    @Override
    public ICCMap apply(CCProcObject proc) {
        ICCMap p = proc.params();
        IDB db = proc.db();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = p.asString(CCConst.p_sql);
        try {
            ps = proc.db().connection().prepareStatement(sql);
            if (p.containsKey(CCConst.p_fields)) {
                this.proc_fill(db, ps, p.list(CCConst.p_fields));
            } else {
                ps_fill.accept(ps, (Object[]) p.get(CCConst.p_params));
            }
            rs = ps.executeQuery();
            ICCList mFields = rs_metadata.apply(db.types(), rs);
            return (rs.next()) ? rs_row.apply(mFields, rs) : null;
        } catch (Exception e) {
        } finally {
            __release(rs);
            __release(ps);
        }
        return null;
    }

}
