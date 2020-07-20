package org.cc.model.field;

import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.type.CCDoubleType;


/**
 *
 * @author William
 */
@IAProxyClass(id = "field.double")
public class JODoubleField extends JONumberField {

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        super.__init__(cfg);
        type = new CCDoubleType();
    }


}
