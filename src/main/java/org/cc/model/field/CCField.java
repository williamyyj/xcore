package org.cc.model.field;

import org.cc.ICCMap;
import org.cc.ICCType;
import org.cc.CCMap;

public class CCField extends CCMap implements ICCField {

    protected ICCType<?> type;


    public CCField() {

    }

    public CCField(String op, String name, ICCType type, String alias) {
        put("id", name.toLowerCase());
        put("name", name);
        this.type = type;
        put("dt", type.dt());
        put("alias", alias);
        put("op", op);
    }

    @Override
    public void __init__(ICCMap cfg) throws Exception {
        putAll(cfg);
    }

    /**
     * 資料庫欄位名
     *
     * @return
     */
    @Override
    public String name() {
        return asString("name", asString("id"));
    }

    /**
     * 系統識別值 （唯一)
     *
     * @return
     */
    @Override
    public String id() {
        return asString("id");
    }

    @Override
    public String dt() {
        return asString("dt");
    }

    @Override
    public String label() {
        return asString("label");
    }

    @Override
    public int dtSize() {
        return asInt("size", 0);
    }

    public String label(String lang) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * DB : P -> pk , F-> FK , I -> index , N -> not null
     *
     * @return
     */
    @Override
    public String ct() {
        return asString("ct", null);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String ft() {
        return asString("ft", null);
    }

    @Override
    public ICCType<?> type() {
        return this.type;
    }

    @Override
    public String alias() {
        return asString("alias", null);
    }

    @Override
    public String fmt() {
        return asString("fmt", null);
    }

    @Override
    public String notes() {
        return asString("notes", null);
    }

    @Override
    public void setFieldValue(ICCMap row, Object value) {
        if (row != null) {
            row.put(name(), type().value(value));
        }
    }

    @Override
    public ICCMap cfg() {
        return this;
    }

    public String op() {
        return asString("op");
    }

    public Object getValue() {
        return get("$fv");
    }

    public Object setValue(Object v) {
        return put("$fv", v);
    }

    @Override
    public Object getFieldValue(ICCMap row) {
        Object ret = null;
        if (row != null) {
            ret = row.get(asString("alias"));
            if (ret == null) {
                ret = row.get(asString("id"));
            }
            if (ret == null) {
                ret = row.get(asString("name"));
            }
            return ret;
        } else {
            return ret;
        }
    }

}
