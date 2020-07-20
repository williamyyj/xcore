package org.cc.model.field;

import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.type.CCStringType;

/**
 * @author William
 */
@IAProxyClass(id = "field.string")
public class JOStringField extends CCField {

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        super.__init__(cfg);
        type = new CCStringType();
    }

   

}
