package org.cc.fun.db;

import org.cc.db.ProcBase;
import java.util.function.Function;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.util.CCFunc;

/**
 *
 * @author william
 */
public class proc_page extends ProcBase implements Function<CCProcObject, ICCList> {

    @Override
    public ICCList apply(CCProcObject proc)  {
        String id = "db.proc_" + proc.db().cfg().asString("database") + "_page";
        return (ICCList) CCFunc.apply(id, proc);
    }

}
