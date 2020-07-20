package org.cc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.cc.CCConfig;
import org.cc.ICCMap;
import org.cc.type.CCTypes;

import org.cc.util.CCLogger;

/**
 *
 * @author william
 */
public abstract class DBBase implements IDB {

    protected static int connCount;

    protected static Map<String, ICCDataSource> mds;
    protected String base;
    protected Connection conn;
    protected ICCMap cfg;
    protected boolean is_reference = false;
    //protected IJOBiFunction<Object, PreparedStatement, Object[]> row_fill;
    protected CCTypes types;
    Function<String, String> fun2alias = new org.cc.fun.text.to_alias();
    Function<String, String> fun2short = new org.cc.fun.text.to_short();

    public DBBase(String base) {
        this(base, "db");
    }

    public DBBase(String base, String dbId) {
        this.base = base;
        this.cfg = new CCConfig(base, dbId).params();
        init_components();
    }

    public DBBase(String base, Connection conn) {
        this(base);
        if (conn != null) {
            is_reference = true;
            this.conn = conn;
        }
    }

    protected void init_components() {
        System.out.println("===== cfg : "+cfg);
        cfg.put("base", this.base);
        if (cfg.containsKey("url")) {
            String url = cfg.asString("url");
            url = url.replace("${base}", this.base);
            cfg.put("url", url);
        }
        //   JOTools.set_default(cfg, "@mrow", "model.FMRS2Row");
        //   JOTools.set_default(cfg, "@mrows", "model.FMRS2Rows");
        //   JOTools.set_default(cfg, "@mfill", "model.FMPSFill");
        //    JOTools.set_default(cfg, "@alias", "util.FldAlias");
        //    JOTools.set_default(cfg, "@short", "util.FldShort");
        //    JOTools.set_default(cfg, "ds", "hyweb.jo.db.DSC3P0");
    }

    @Override
    public Connection connection() throws SQLException {
        if (conn == null) {
            conn = ds().getConnection();
            connCount++;
        }
        return conn;
    }

    protected String id() {
        return cfg.asString("id");
    }

    protected ICCDataSource ds() {
        String id = id();
        ICCDataSource ds = mds().get(id);
        if (ds == null) {
             //String classId = cfg.asString("ds", "org.cc.db.DSC3P0");
           // String classId = cfg.asString("ds", "org.cc.db.DSHikariCP");
           String classId = cfg.asString("ds", "org.cc.db.DSTomcatPool");
            CCLogger.info("classId : " + classId);
            try {
                ds = (ICCDataSource) Class.forName(classId).newInstance();
                ds.init(cfg);
                mds.put(id, ds);
            } catch (Exception ex) {
                CCLogger.error("", ex);
            }
        }
        return ds;
    }

    protected Map<String, ICCDataSource> mds() {
        if (mds == null) {
            mds = new HashMap();
        }
        return mds;
    }

    public static void __release(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
            rs = null;
        }
    }

    public static void __release(PreparedStatement ps) throws SQLException {
        if (ps != null) {
            ps.close();
            ps = null;
        }
    }

    protected void __release(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
            connCount--;
            conn = null;
        }
    }

    @Override
    public void release() {
        try {
            if (!is_reference) {
                __release(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ICCMap cfg() {
        return cfg;
    }

    @Override
    public String base() {
        return base;
    }

    @Override
    public String catalog() {
        return cfg.asString("catalog");
    }

    @Override
    public String schema() {
        return cfg.asString("schema");
    }

    @Override
    public String database() {
        return (cfg != null) ? cfg.asString("database") : null;
    }

    @Override
    public String to_alias(String text) {
        return fun2alias.apply(text);
    }

    @Override
    public String to_short(String text) {
        return fun2short.apply(text);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Override
    public void shutdown() throws Exception {
        if (mds != null) {
            Set<Map.Entry<String, ICCDataSource>> entry = mds.entrySet();
            for (Map.Entry<String, ICCDataSource> e : entry) {
                e.getValue().close();
            }
        }
    }

    @Override
    public String status() {
        return "{  \"conn\":" + connCount + " \"mds\":" + mds + "\"}";
    }

    public static String info() {
        return "{  \"conn\":" + connCount + " \"mds\":" + mds + "\"}";
    }

}
