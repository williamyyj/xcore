package org.cc.fun.db;

import org.cc.CCConst;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.util.CCFunc;
import org.junit.Test;


/**
 * @author william
 */
public class DBTest {


    public void test_row() throws Exception {
        CCProcObject proc = new CCProcObject(CCConst.base("baphiq"));
        try {
            // 不建議直直接使用  利用 meta query 的方式為主
            proc.params().put(CCConst.p_sql, "select * from psStore where dataId=?");
            proc.params().put(CCConst.p_params, new Object[]{"90002"});
            ICCMap row = (ICCMap) CCFunc.apply("db.proc_row", proc);
            System.out.println(row);
        } finally {
            proc.release();
        }
    }

    @Test
    public void test_rows() throws Exception {
        CCProcObject proc = new CCProcObject(CCConst.base("baphiq"));
        try {
            proc.params().put(CCConst.p_sql, "select * from psStore");
            ICCList rows = (ICCList) CCFunc.apply("db.proc_rows", proc);
            for (Object row : rows) {
                System.out.println(row);
            }
        } finally {
            proc.release();
        }
    }
    

}
