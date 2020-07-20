package org.cc.model;

import org.cc.model.field.ICCField;
import org.cc.CCMap;
import org.cc.ICCMap;
import org.cc.ICCResource;
import org.cc.db.DB;
import org.cc.db.IDB;
import org.cc.util.CCPath;

/**
 * @author william
 */
public class CCProcObject extends CCMap implements ICCResource {


    public final static String pre_metadata = "$metadata";
    public final static String pre_module = "$module";
    public final static String pre_cfg = "$cfg";
    public final static String pre_ff = "$ff"; //  field function 
    public final static String pre_fp = "$fp"; //  表單回傳或初始資料
    public final static String pre_fields = "$fields"; //  表單回傳或初始資料
    public final static String act = "$act";
    public final static String cmd = "$cmd";
    public final static int attr_self = 0;
    public final static int attr_params = 1;
    public final static int attr_request = 2;
    public final static int attr_session = 3;
    public final static int attr_app = 4;

    protected String base;
    protected String dbId;
    public CCProcObject(String base) {
        this(base,"db");
    }

    public CCProcObject(String base, String dbId) {
        this.base = base;
        this.dbId = dbId;
        put(pre_fp, new CCMap());
        put(pre_ff, new CCMap());
        put(pre_fields, new CCMap());
    }

    @Override
    public void release() {
        db().release();
    }

    public ICCMap fp() {
        return map(pre_fp);
    }

    /*
     *   monk object 
     */
    public Object get(int fld, String name, Object dv) {
        switch (fld) {
            case attr_self:
                return (containsKey(name)) ? get(name) : dv;
            case attr_params:
                return fp().containsKey(name) ? fp().get(name) : dv;
            case attr_request:
                return containsKey("$req_" + name) ? get("$req_" + name) : dv;
            case attr_session:
                return containsKey("$sess_" + name) ? get("$sess_" + name) : dv;
            case attr_app:
                return containsKey("$app_" + name) ? get("$app_" + name) : dv;
        }
        return dv;
    }

    public Object set(int fld, String name, Object value) {
        switch (fld) {
            case attr_self:
                return put(name, value);
            case attr_params:
                return fp().put(name, value);
            case attr_request:
                return put("$req_" + name, value);
            case attr_session:
                return put("$sess_" + name, value);
            case attr_app:
                return put("$app_" + name, value);
        }
        return null;
    }

    public String base() {
        return this.base;
    }

    public IDB db() {
        return (IDB) this.getOrDefault(dbId, init_db());
    }

    private Object init_db() {
        IDB db = (IDB) get(dbId);
        if (db == null) {
            db = new DB(base,dbId);
            put(dbId, db);
        }
        return db;
    }

    /**
     * metaId metaId:alias perfix:metaId:alias
     *
     * @param line
     * @return
     */
    public CCMetadata metadata(String line) {
        String[] items = line.split(":");
        switch (items.length) {
            case 1:
                return proc_metadata(items[0]);
            case 2:
                return proc_metadata(items[0], items[1]);
            case 3:
                return proc_metadata(items[0], items[1], items[3]);
            default:
                return null;
        }

    }

    private CCMetadata proc_metadata(String metaId) {
        String id = pre_metadata + ":" + metaId;
        CCMetadata md = (CCMetadata) CCPath.path(this, id);
        if (md == null) {
            md = new CCMetadata(base, metaId);
            CCPath.set(this, id, md);
            map(pre_fields).putAll(md.fields());
        }
        return md;
    }

    private CCMetadata proc_metadata(String metaId, String alias) {
        String id = pre_metadata + ":" + metaId;
        CCMetadata md = (CCMetadata) CCPath.path(this, id);
        if (md == null) {
            md = new CCMetadata(base, metaId, alias);
            CCPath.set(this, id, md);
            map(pre_fields).putAll(md.fields());
        }
        return md;
    }

    private CCMetadata proc_metadata(String prefix, String metaId, String alias) {
        String id = pre_metadata + ":" + metaId;
        CCMetadata md = (CCMetadata) CCPath.path(this, id);
        if (md == null) {
            if (prefix.length() == 0) {
                md = new CCMetadata(base, metaId, alias);
            } else {
                md = new CCMetadata(base, prefix, metaId, alias);
            }
            CCPath.set(this, id, md);
            map(pre_fields).putAll(md.fields());
        }
        return md;
    }

    public Object ff(String ffid, ICCMap row, String id, Object dv) {
        ICCFF ff = (ICCFF) map(pre_ff).get(ffid);
        return (ff == null) ? dv : ff.as(row, id);
    }

    public ICCMap params() { //
        if (!this.containsKey("$")) {
            put("$", new CCMap());
        }
        return map("$");
    }

    public ICCField field(String fid) {
        return (ICCField) map(pre_fields).get(fid);
    }

}
