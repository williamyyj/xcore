package org.cc.model;

import org.cc.ICCMap;



/**
 * @author william
 * @param <R>
 */
public interface ICCFF<R> {

    public void init(CCProcObject proc) throws Exception ; 
    
    public R as(ICCMap row, String id);
    
    public ICCMap cfg();

}
