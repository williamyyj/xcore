package org.cc.model.field;

import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.type.CCClobType;

/**
 *
 * @author william
 */




@IAProxyClass(id = "field.clob")
public class JOClobField extends CCField {

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        super.__init__(cfg);
        type = new CCClobType();
    }
    
}
