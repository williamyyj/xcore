package org.cc.fun.db;

import org.cc.db.ProcBase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Function;
import org.cc.CCConst;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.ICCMap;


/**
 *
 * @author william
 */
public class proc_rows extends ProcBase implements Function<CCProcObject, ICCList> {

    @Override
    public ICCList apply(CCProcObject proc) {
        ICCMap p = proc.params();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = p.asString(CCConst.p_sql);
        try {
            ps = proc.db().connection().prepareStatement(sql);
            if (p.containsKey(CCConst.p_fields)) {
                this.proc_fill(proc.db(), ps, p.list(CCConst.p_fields));
            } else {
                ps_fill.accept(ps, (Object[]) p.get(CCConst.p_params));
            }
            rs = ps.executeQuery();
            ICCList mFields = rs_metadata.apply(proc.db().types(), rs);
            return rs_rows.apply(mFields, rs);
        } catch(Exception e){
        } finally {
            __release(rs);
            __release(ps);
        }
        return null;
    }

}
