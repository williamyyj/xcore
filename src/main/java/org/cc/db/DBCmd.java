package org.cc.db;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;
import org.cc.CCConst;
import org.cc.CCList;
import org.cc.CCMap;
import org.cc.ICCMap;
import org.cc.model.CCProcObject;
import org.cc.model.CCWorkObject;

/**
 * @author William 整合DBJO ${base}/dp/xxxx.dao 統一使用 支援同文件資料靜態載入 ${ rem ,
 * xxxxxxxxxxxxxxxxxxxxxx} 註解 ${var,dt} 變數模式 ${ op:fld_name:dt} 動態欄位 fld_name op
 * map(fld_name) ${range:fld_name:dt} fld_name beteen map(fld_name_1) and
 * map(fld_name_2) ${like: fld_name} only 字串欄位 會利用 IDB 的 base() 取到 base 位置
 */
public class DBCmd {

    protected final static Pattern p = Pattern.compile("\\$\\{([^\\}]+)\\}");

    public static Map<String, String> mop;

    public static Map<String, String> op() {
        if (mop == null) {
            mop = new HashMap<String, String>();
            mop.put("=", "=");
            mop.put(">", ">");
            mop.put(">=", ">=");
            mop.put("<", "<");
            mop.put("<=", "<=");
            mop.put("$like", "like"); //   xxx%
            mop.put("$all", "like");   //   %xxxx%
            // mop.put("$range", "");  //     fld  betten a and b 
            mop.put("$set", "="); // for update 
            mop.put("$rm", "");  // 移除最後 ","   
            mop.put("@", "");  //    for table query or const 
            mop.put("$expr", "$expr");
        }
        return mop;
    }

    public static ICCMap parser_cmd(CCWorkObject wo) throws Exception {
        ICCMap mq = new CCMap();
        mq.put(CCWorkObject.wo_fields, new CCList());
        Stack<StringBuffer> stack = new Stack<StringBuffer>();
        String cmd = wo.event().asString("$cmd");
        Matcher match = p.matcher(cmd);
        StringBuffer sql = new StringBuffer();
        while (match.find()) {
            String item = match.group(1);
            match.appendReplacement(sql, ""); // 直接清空
            if ("or".equals(item)) {
                stack.push(sql);
                sql = new StringBuffer();
            } else if ("end".equals(item)) {
                if (sql.length() > 0) {
                    String child = sql.toString().replaceFirst("and", "");
                    child = child.replaceAll("and", "or");
                    sql = stack.pop();
                    sql.append(" and (").append(child).append(" )");
                } else {
                    sql = stack.pop();
                }
            } else if (!item.startsWith("rem")) {
                DBCmdItem.process_item(wo.proc().db(), sql, mq, wo.p(), item);
            }
        }
        match.appendTail(sql);
        mq.put(CCWorkObject.wo_sql, sql);
        return mq;

    }

    public static ICCMap parser_cmd(CCProcObject proc, String cmd) throws Exception {
        ICCMap mq = new CCMap();
        mq.put(CCConst.p_fields, new CCList());
        Stack<StringBuffer> stack = new Stack<StringBuffer>();

        Matcher match = p.matcher(cmd);
        StringBuffer sql = new StringBuffer();
        while (match.find()) {
            String item = match.group(1);
            match.appendReplacement(sql, ""); // 直接清空
            if ("or".equals(item)) {
                stack.push(sql);
                sql = new StringBuffer();
            } else if ("end".equals(item)) {
                if (sql.length() > 0) {
                    String child = sql.toString().replaceFirst("and", "");
                    child = child.replaceAll("and", "or");
                    sql = stack.pop();
                    sql.append(" and (").append(child).append(" )");
                } else {
                    sql = stack.pop();
                }
            } else if (!item.startsWith("rem")) {
                DBCmdItem.process_item(proc.db(), sql, mq, proc.params(), item);
            }
        }
        match.appendTail(sql);
        mq.put(CCConst.p_sql, sql);
        return mq;
    }

}
