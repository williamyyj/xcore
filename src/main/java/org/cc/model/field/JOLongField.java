package org.cc.model.field;

import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.type.CCLongType;



/**
 * @author William
 */
@IAProxyClass(id = "field.long")
public class JOLongField extends JONumberField {

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        super.__init__(cfg);
        type = new CCLongType();
    }



}
