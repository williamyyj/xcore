package org.cc.type;

import org.cc.ICCInit;

import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: william
 * Date: 2013/7/29
 * Time: 下午 5:31
 * To change this template use File | Settings | File Templates.
 */
public class mssql_init implements ICCInit<CCTypes> {
    @Override
    public void __init__(CCTypes self) throws Exception {
        self.put(Types.DATE,new ms_date());
        self.put(Types.TIME,new ms_date());
        self.put(Types.TIMESTAMP,new ms_date());
    }
}
