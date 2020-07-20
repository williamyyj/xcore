package org.cc.fun.cg;

import org.cc.CCConst;
import org.cc.CCMap;
import org.cc.model.CCProcObject;
import org.cc.ICCMap;
import org.cc.util.CCFunc;
import org.junit.Test;


/**
 *
 * @author william
 */
public class JavaBeanTest {

    @Test
    public void test_exec() {
        CCProcObject proc = new CCProcObject(CCConst.base("webPos"));
        try {
            CCMap p = new CCMap();
            p.put("metaId", "psSprayPerson");
            //p.put("cgObject", "$do");
            ICCMap m = (ICCMap) CCFunc.apply2("cg.DPJavaBean", proc, p);
            System.out.println(m.toString(4));
        } catch (Exception e) {
            proc.release();
        }
    }
}
