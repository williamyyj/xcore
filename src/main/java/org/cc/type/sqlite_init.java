package org.cc.type;

import org.cc.ICCInit;

import java.sql.Types;
import org.cc.ICCType;

/**
 * Created with IntelliJ IDEA. User: william Date: 2013/7/29 Time: 下午 5:31 To
 * change this template use File | Settings | File Templates.
 */
public class sqlite_init implements ICCInit<CCTypes> {

    @Override
    public void __init__(CCTypes self) throws Exception {
        ICCType date_type = new SQLiteDateType();
        self.put(ICCType.dt_date, date_type);
        self.put(Types.DATE, date_type);
        self.put(Types.DATE, date_type);
        self.put(Types.TIME, date_type);
        self.put(Types.TIMESTAMP, date_type);
    }
}
