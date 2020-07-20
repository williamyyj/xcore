package org.cc;

/**
 *
 * @author william
 */
public interface ICCBatch {

    public void execute(ICCMap params) throws Exception;

    public void release() throws Exception;
    
}
