package org.cc.model;

import org.cc.ICCList;
import org.cc.util.CCFunc;
import org.cc.util.CCLogger;
import org.cc.util.CCPath;

/**
 *
 * @author william
 */
public class CCProcUtils {

    private final static String CHK_STATUS = "$check:status";

    /**
     * @param proc
     * @param cmdString
     * @return
     */
    public static Object exec(CCProcObject proc, String cmdString) {
        Object ret = null;

        CCProcCmdString cmd = new CCProcCmdString(cmdString);
        try {
            if (!"$".equals(cmd.inParam())) {
                proc.put("$$", proc.get("$"));// 客制化處理
                proc.put("$", proc.get(cmd.inParam()));
            }
            ret = CCFunc.apply2(cmd.funId(), proc, cmd.params());
            if (ret != null) {
                CCPath.set(proc, cmd.outParam(), ret);
            }
        } catch (Exception e) {
            CCLogger.info("Can't apply :" + cmd, e);
            setException(proc, cmd.funId(), e.getMessage());
        }

        return ret;
    }

    public static void each(CCProcObject proc, String cmdString) {
        CCProcCmdString cmd = new CCProcCmdString(cmdString);
        ICCList data = ("$".equals(cmd.inParam())) ? proc.list("$data") : proc.list(cmd.inParam());
        if (data != null) {
            int idx = 0 ;
            for (Object o : data) {
                proc.put("$", o);
                exec(proc, cmdString);
                System.out.println("===== item idx : "+idx++);
            }
        }
    }

    public static void setException(CCProcObject proc, String funId, String msg) {
        CCPath.set(proc, CHK_STATUS, "E");
        CCPath.set(proc, "$check:exception", msg);
        CCPath.set(proc, "$check:funId", funId);
    }

}
