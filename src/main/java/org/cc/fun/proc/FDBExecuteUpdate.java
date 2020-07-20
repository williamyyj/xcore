package org.cc.fun.proc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiFunction;
import org.cc.CCConst;
import org.cc.ICCMap;
import org.cc.db.ProcBase;
import org.cc.model.CCProcObject;

/**
 * executeUpdate int executeUpdate(String sql) 用於執行 INSERT、UPDATE 或 DELETE 語句以及
 * SQL DDL(資料定義語言)語句,例如 CREATE TABLE 和 DROP TABLE。INSERT、UPDATE 或 DELETE
 * 語句的效果是修改表中零行或多行中的一列或多列。
 *
 * @author william
 */
public class FDBExecuteUpdate extends ProcBase implements BiFunction<CCProcObject, ICCMap, Integer> {

    @Override
    public Integer apply(CCProcObject proc, ICCMap jq) {
        PreparedStatement ps = null;
        String sql = jq.asString(CCConst.p_sql);
        try {
            ps = proc.db().connection().prepareStatement(sql);
            proc_fill(proc.db(), ps, jq.list(CCConst.p_fields));
           
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            __release(ps);
        }
        return -1;
    }

}
