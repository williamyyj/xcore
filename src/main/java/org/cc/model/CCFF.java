package org.cc.model;

import org.cc.model.field.ICCField;
import java.util.Map;
import org.cc.ICCInit;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.util.CCJSON;
import org.cc.util.CCLogger;
import org.cc.util.CCPath;

/**
 * @author william
 */
public class CCFF {

    public static String prefix = "$ff";

    public static String pkg = "org.cc.ff";

    public static String pffId(String id) {
        return prefix + "_" + id;
    }

    public static ICCFF getRequestFF(CCProcObject proc, String id) {
        return (ICCFF) proc.get(CCProcObject.attr_request, pffId(id), null);
    }

    public static void setRequestFF(CCProcObject proc, String id, ICCFF ff) {
        proc.set(CCProcObject.attr_request, pffId(id), ff);
    }

    public static ICCFF ff_create(CCProcObject proc, String ffId, ICCMap cfg) {
        try {
            String classId = pkg + "." + ffId;
            Class cls = Class.forName(classId);
            Object o = cls.newInstance();
            ((ICCInit) o).__init__(cfg);
            ((ICCFF) o).init(proc);

            return (ICCFF) o;
        } catch (Exception ex) {
            CCLogger.error("Can't find " + cfg, ex);
        }
        return null;
    }

    public static ICCFF ff_create(CCProcObject proc, CCMetadata md, Object line) {
        ICCMap cfg = CCFF.config(md, line);
        String classId = cfg.asString("$ff");
        return ff_create(proc, classId, cfg);
    }

    public static ICCMap config(CCMetadata md, Object line) {
        ICCMap ff = CCJSON.line(line);
        String fid = ff.asString("$id");
        ICCField fld = (md != null) ? md.fields().get(fid) : null;
        if (fld != null) {
            ff.put("$fld", fld.cfg());
        }
        return ff;
    }

    public static Map<String, ICCFF> meta_ff(CCProcObject proc, String metaId) {
        Map<String, ICCFF> m = (Map<String, ICCFF>) proc.get("$ff");
        ICCList ffs = proc.metadata(metaId).cfg().list("$ff");
        if (ffs != null) {
            for (int i = 0; i < ffs.size(); i++) {
                ICCFF ff = ff_create(proc, proc.metadata(metaId), ffs.get(i));
                m.put(ff.cfg().asString("$id"), ff);
            }
        }
        return m;
    }

    public static ICCFF proc_ff(CCProcObject proc, Object line) {
        ICCMap cfg = CCFF.config(null, line);
        ICCFF ff = ff(proc, cfg.asString("$id"));
        if (ff == null) {
            String classId = cfg.asString("$ff");
            ff = ff_create(proc, classId, cfg);
        } else {
            CCLogger.info("Dup load  ff : "+line);
        }
        CCPath.set(proc, CCProcObject.pre_ff + ":" + ff.cfg().asString("$id"), ff);
        return ff;
    }

    public static ICCFF ff(CCProcObject proc, String id) {
        return (ICCFF) proc.map(CCProcObject.pre_ff).get(id);
    }

    public static String dv(Object fv, String dv) {
        return (fv != null) ? fv.toString().trim() : dv;
    }
}
