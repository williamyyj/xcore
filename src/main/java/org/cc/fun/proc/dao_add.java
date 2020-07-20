package org.cc.fun.proc;

import java.util.function.BiFunction;
import org.cc.ICCMap;
import org.cc.db.DBCmd;
import org.cc.log.CCLogger;
import org.cc.model.CCMetadata;
import org.cc.model.CCProcObject;
import org.cc.util.CCFunc;

/**
 * @author william
 */
public class dao_add implements BiFunction<CCProcObject, String, Integer> {

    @Override
    public Integer apply(CCProcObject proc, String cmdLine) {
        try {
            CCMetadata md = proc.metadata(cmdLine);
            String sql = (String) CCFunc.apply("dp.FSQLInsert", md.mdFields());
            ICCMap jq = DBCmd.parser_cmd(proc, sql);
            return (Integer) CCFunc.apply2("proc.FDBExecuteUpdate", proc, jq);
        } catch (Exception ex) {
            CCLogger.info("Can't add " + cmdLine+":::" + proc.params(), ex);
        }
        return -1;
    }

}
