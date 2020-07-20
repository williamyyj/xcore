package org.cc.model.field;



import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.type.CCDateType;

@IAProxyClass(id = "field.date")
public class JODateField extends CCField {

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        super.__init__(cfg);
        type = new CCDateType();
    }
}
