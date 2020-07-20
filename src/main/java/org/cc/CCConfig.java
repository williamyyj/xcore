package org.cc;

import org.cc.util.CCJSON;
import org.cc.util.CCLogger;

/**
 *
 * @author william
 */
public class CCConfig {

    private ICCMap pcfg; // public config
    private ICCMap cfg;
    private String base;
    private String oid;   // object id 

    public CCConfig(String base, String id) {
        init(base, id);
    }

    private void init(String base, String id) {
        this.base = base;
        this.oid = id;
        pcfg = CCJSON.load(base, "cfg");

        if (pcfg != null) {
            int version = pcfg.asInt("version");
            switch (version) {
                case 1:
                    init_version01(pcfg);
                    break;
                default:
                    init_version00(pcfg);
            }

        }
        init_params();
    }

    private void init_params() {
        //  
    }

    private void init_version00(ICCMap pcfg) {
        cfg = CCJSON.load(base + "/config", oid);
        String scope = pcfg.asString("scope");
        cfg = (cfg != null) ? cfg.map(scope) : null;
    }

    /**
     * 相容舊版設定方式
     */
    private void init_version01(ICCMap pcfg) {
        String scope = pcfg.asString("scope");
        if ("".equals(scope)) {
            scope = System.getProperty("scope"); // 測試用或未來系統設定
        }
        String path = pcfg.asString("config_path", base + "/config") + "/" + scope;
        CCLogger.info("===== config path : " + path);
        cfg = CCJSON.load(path, oid);
    }

    public ICCMap params() {
        return this.cfg;
    }

    public ICCMap pcfg() {
        return pcfg;
    }

}
