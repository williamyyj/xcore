package org.cc.fun.cg;

import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;
import org.cc.CCList;
import org.cc.CCMap;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.model.CCMetadata;
import org.cc.model.field.ICCField;
import org.cc.util.CCJSON;
import org.cc.util.CCMvel;

/**
 * 產出 JavaBean 使用的資料
 *
 * @author william
 */
public class DPJavaBean  implements BiFunction<CCProcObject, ICCMap, ICCMap> {

    @Override
    public ICCMap apply(CCProcObject proc, ICCMap p) {

        String metaId = p.asString("metaId");
        String cgObject = p.asString("cgObject", "$do");
        ICCMap cg = CCJSON.load(proc.base(), "cg");
        p.putAll(cg);
        CCMvel.eval(p, cgObject);
        ICCMap m = new CCMap(p);
        CCMetadata md = proc.metadata(metaId);
        Map<String, ICCField> flds = md.fields();
        ICCList fldList = new CCList();
        flds.forEach((k, v) -> {
            String dt = v.dt();
            String id = v.id();
            if (!"table".equals(dt)) {
                String nt = CGJava.dt2NativeMap.getOrDefault(dt, "Object");
                String mthName = id.substring(0, 1).toUpperCase() + id.substring(1);
                v.cfg().put("nt", nt);
                v.cfg().put("mthName", mthName);
                fldList.add(v);
            } else {
                String classId = id.substring(0, 1).toUpperCase() + id.substring(1);
                m.put("classId", classId+p.asString("cls"));   
            }

        });
        m.put("flds", fldList);
             m.put("now", new Date());
        return m;
    }

}
