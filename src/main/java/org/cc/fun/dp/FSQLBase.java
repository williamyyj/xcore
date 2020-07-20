package org.cc.fun.dp;



import java.util.HashMap;
import java.util.Map;

/**
 * @author william
 *    ft :   field  選取的欄位
 *    ft:   query 查詢的欄位
 *    ft:   table  table 欄位
 *    
 *    
 */
public abstract class FSQLBase {
    
    private static Map<String, String> op;

    public static Map<String, String> op() {
        if (op == null) {
            op = new HashMap<String, String>();
            op.put("=", "=");
            op.put(">", ">");
            op.put(">=", ">=");
            op.put("<", "<");
            op.put("<=", "<=");
            op.put("$like", "like"); //   xxx%
            op.put("$all", "like");   //   %xxxx%
            op.put("$range", "");  //     fld  betten a and b 
            op.put("$set", "="); // for update 
            op.put("$rm", ""); // for clean ; 
        }
        return op;
    }
    
}
