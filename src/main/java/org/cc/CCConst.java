/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc;

/**
 *
 * @author william
 */
public class CCConst {

    public static String root = "D:\\HHome\\GoogleDrive\\myjob\\resources\\project";

    public static String base(String base, String pid) {
        String path = base + "\\" + pid;
        path = path.replaceAll("[\\\\|.]", "/");
        return path;
    }

    public static String base(String pid) {
        String rp = System.getProperty("root", root);
        return base(rp, pid);
    }
    
    public final static String p_sql = "$p_sql";
    public final static String p_fields = "$p_fields";
    public final static String rs_fields = "$rs_fields";  // 取消
    public final static String ps_fields = "$ps_fields"; // 取消 
    public final static String p_params = "$p_params";
    public final static String db_fields = "$db_fields";
    public final static String meta_fields = "$tbFields";
    
}
