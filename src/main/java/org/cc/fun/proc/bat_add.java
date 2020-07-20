/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.fun.proc;


import java.util.function.BiFunction;
import org.cc.db.DBCmdPS;
import org.cc.log.CCLogger;

import org.cc.model.CCMetadata;
import org.cc.model.CCProcObject;
import org.cc.util.CCFunc;


/**
 *
 * @author william
 */
public class bat_add implements BiFunction<CCProcObject, String, int[]> {

    @Override
    public int[] apply(CCProcObject proc, String cmdLine) {
        try {
            CCMetadata md = proc.metadata(cmdLine);
            String sql = (String) CCFunc.apply("dp.FSQLInsert", md.mdFields());
            DBCmdPS cmd = new DBCmdPS(proc,sql);
            CCFunc.apply2("proc.FDBBatchUpdate", proc, cmd);  
        } catch (Exception ex) {
            CCLogger.info("Can't add " + cmdLine+":::" + proc.params(), ex);
        }
        return null;
    }

}
