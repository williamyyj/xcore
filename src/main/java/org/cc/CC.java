/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc;

import org.cc.model.CCProcCmdString;
import org.cc.model.CCProcObject;
import org.cc.util.CCFunc;

/**
 *
 * @author william
 */
public class CC {

    /**
     * @param proc
     * @param cmd
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
                proc.put(cmd.outParam(), ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
