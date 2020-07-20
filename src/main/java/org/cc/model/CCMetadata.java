package org.cc.model;

import org.cc.model.field.ICCField;
import org.cc.model.field.CCFieldUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.util.CCJSON;

/**
 * 只提供標準資料庫操作
 *
 * @author william
 */
public class CCMetadata {

    private String base;

    private String prefix;

    private String alias;

    private static String prjPrefix = "/module/$meta"; //  專案開發
    private static String prodPrefix = "/dp/metadata"; // 產品

    private ICCMap cfg;

    private Map<String, ICCField> _fields;

    public CCMetadata(String base, String prefix, String metaId) {
        this(base, prjPrefix, metaId, null);
    }

    public CCMetadata(String base, String metaId) {
        this(base, prjPrefix, metaId, null);
    }

    public CCMetadata(String base, String prefix, String metaId, String alias) {
        super();
        this.base = base;
        this.prefix = prefix;
        this.alias = alias;
        _fields = new LinkedHashMap<>();
        this.load_metadata(metaId);
    }

    private void load_metadata(String metaId) {
        cfg = CCJSON.load(base + prefix, metaId);
        if (cfg.containsKey("meta")) {
            ICCList meta = cfg.list("meta");
            meta.stream().forEach((Object o) -> {
                try {
                    ICCMap item = (ICCMap) o;
                    ICCField field = CCFieldUtils.newInstance(item);
                    _fields.put(item.asString("id"), field);
                    if (alias != null) {
                        _fields.put(alias + "." + item.asString("id"), field);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            });
        }
    }

    public Map<String, ICCField> fields() {
        return _fields;
    }

    public List<ICCField> mdFields() {
        List<ICCField> ret = new ArrayList<>();
        String[] flds = cfg.asString("$tbFields").split(",");
        for (String fld : flds) {
            ret.add(_fields.get(fld));
        }
        return ret;
    }

    public ICCMap cfg() {
        return cfg;
    }

    /**
     * @param eventId
     * @return
     * @deprecated As of release 1.0.1, replaced by {@link #getPreferredSize()}
     * 請使用module
     */
    @Deprecated
    public ICCMap event(String eventId) {
        return cfg.map(eventId);
    }

}
