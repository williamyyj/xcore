package org.cc.json;



/**
 *
 * @author william
 */
public class CCConfig {

    private JSONObject pcfg; // public config
    private JSONObject cfg;
    private String base;
    private String oid;   // object id 

    public CCConfig(String base, String id) {
        init(base, id);
    }

    private void init(String base, String id) {
        this.base = base;
        this.oid = id;
        pcfg = CCCache.load(base, "cfg");

        if (pcfg != null) {
            int version = pcfg.optInt("version");
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

    private void init_version00(JSONObject pcfg) {
        cfg = CCCache.load(base, "config",oid);
        String scope = pcfg.optString("scope");
        cfg = (cfg != null) ? cfg.optJSONObject(scope) : null;
    }

    /**
     * 相容舊版設定方式
     */
    private void init_version01(JSONObject pcfg) {
        String scope = pcfg.optString("scope");
        if ("".equals(scope)) {
            scope = System.getProperty("scope"); // 測試用或未來系統設定
        }
        String path = pcfg.optString("config_path", base + "/config") + "/" + scope;
       // JOLogger.info("===== config path : " + path);
        cfg = CCCache.load(path, oid);
    }

    public JSONObject params() {
        return this.cfg;
    }

    public JSONObject pcfg() {
        return pcfg;
    }

}
