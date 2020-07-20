package org.cc.model.field;

import org.cc.IAProxyClass;
import org.cc.type.CCStringType;



/**
 * @author william 視表 or 資料表欄位
 */
@IAProxyClass(id = "field.table")
public class JOTBField extends CCField {

    public JOTBField() {

    }

    public JOTBField(String id, String name) {
        put("dt", "table");
        put("id", id);
        put("name", name);
        type = new CCStringType();
    }

  

}
