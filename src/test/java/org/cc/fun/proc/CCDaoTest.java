package org.cc.fun.proc;

import org.cc.model.CCProcObject;
import org.cc.util.CCFunc;
import org.junit.Test;

/**
 *
 * @author william
 */
public class CCDaoTest {

    @Test
    public void test_dao_add() {
        String base = System.getProperty("$base", "D:\\Users\\william\\Dropbox\\resources\\project\\stock");
        CCProcObject proc = new CCProcObject(base);
        try {
            // CCFunc.apply2("proc.dao_add", proc, "mstock");

            CCFunc.apply2("proc.bat_add", proc, "mstock");
        } catch (Exception e) {
            proc.release();
        }
    }

}
