package org.cc.fun.db;

import org.cc.db.ProcBase;
import java.util.function.Function;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.util.CCLogger;

/**
 *
 * @author william
 */
public class proc_mssql_page extends ProcBase implements Function<CCProcObject, ICCList> {

    @Override
    public ICCList apply(CCProcObject proc) {
        CCLogger.debug("using");
        return null;
    }
    
}
