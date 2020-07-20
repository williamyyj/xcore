package org.cc.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.ICCType;

import org.cc.model.CCProcObject;
import org.cc.model.field.CCField;
import org.cc.util.CCJSON;

/**
 *
 * @author william
 */
public class DBCmdPS extends DBCmd {

    private final CCProcObject proc;
    private final String cmd;
    private List<CCField> pFields;
    private StringBuffer pCmd;

    public DBCmdPS(CCProcObject proc, String cmd) {
        this.proc = proc;
        this.cmd = cmd;
        __init__();
    }

    private void __init__() {
        pFields = new ArrayList<>();
        Stack<StringBuffer> stack = new Stack<StringBuffer>();
        Matcher match = p.matcher(cmd);
        pCmd = new StringBuffer();

        while (match.find()) {
            String item = match.group(1);
            match.appendReplacement(pCmd, ""); // 直接清空
            if ("or".equals(item)) {
                stack.push(pCmd);
                pCmd = new StringBuffer();
            } else if ("end".equals(item)) {
                if (pCmd.length() > 0) {
                    String child = pCmd.toString().replaceFirst("and", "");
                    child = child.replaceAll("and", "or");
                    pCmd = stack.pop();
                    pCmd.append(" and (").append(child).append(" )");
                } else {
                    pCmd = stack.pop();
                }
            } else if (!item.startsWith("rem")) {
                // DBCmdItem.process_item(proc.db(), pCmd, mq, proc.params(), item);
                proc_item(item);
            }
        }
        match.appendTail(pCmd);
    }

    public void proc_item(String item) {
        String line = item.trim();
        String[] args = null;
        if (line.charAt(0) == '[') {
            ICCList arr = CCJSON.loadJA(line);
            args = arr.toArray(new String[0]);
        } else {
            args = item.split(",");
        }
        String name = args[0];
        if (name.charAt(0) == '@') {
            proc_const(args);
        } else if (op().containsKey(name)) {
            process_op(args);
        } else {
            proc_var(args);
        }

    }

    private void proc_const(String[] args) {
        // 這是必填 or 有預設值
        String id = args[0];
        String v = proc.params().asString(id, null);
        if (v != null) {
            pCmd.append(v);
        }
    }

    private void proc_var(String[] args) {
        //    ${field,dt}  |   ${field,dt,alias} 
        //System.out.println(Arrays.toString(args));
        String name = args[0];
        String dt = args[1];
        String alias = (args.length > 2) ? args[2] : null;
        ICCType type = proc.db().types().type(dt);
        CCField fld = new CCField(null, name, type, alias);
        Object v = fld.getFieldValue(proc.params());
        pFields.add(fld);
        pCmd.append("?");
    }

    private void process_op(String[] args) {
        String op = args[0];
        String name = (args.length > 1) ? args[1] : null;
        String dt = (args.length > 2) ? args[2] : null;
        String alias = (args.length > 3) ? args[3] : null;
        ICCType type = proc.db().types().type(dt);
        CCField fld = new CCField(op, name, type, alias);
        if (null != op) {
            switch (op) {
                case "=":
                case ">":
                case ">=":
                case "<":
                case "<=":
                    proc_op2(fld);
                    break;
                case "$like":
                    proc_like(fld);
                    break;
                case "$all":
                    proc_all(fld);
                    break;
                case "$set":
                    proc_set(fld);
                    break;
                case "$rm":
                    proc_rm(fld);
                    break;
                default:
                    break;
            }
        }
    }

    private void proc_op2(CCField fld) {
        Object v = fld.getFieldValue(proc.params());
        if (v != null) {
            pFields.add(fld);
            pCmd.append(" and ").append(fld.name()).append(' ').append(fld.op()).append(" ?");
        }
    }

    private void proc_like(CCField fld) {
        Object v = fld.getFieldValue(proc.params());
        if (v != null) {
            pFields.add(fld);
            fld.setValue(v + "%");
            pCmd.append(" and ").append(fld.name()).append(" like ").append(" ?");
        }
    }

    private void proc_all(CCField fld) {
        Object v = fld.getFieldValue(proc.params());
        if (v != null) {
            pFields.add(fld);
            fld.setValue("%" + v + "%");
            pCmd.append(" and ").append(fld.name()).append(" like ").append(" ?");
        }
    }

    private void proc_set(CCField fld) {
        Object v = fld.getFieldValue(proc.params());
        if (v != null) {
            pFields.add(fld);
            pCmd.append(' ').append(fld.name()).append(" = ").append(" ? ,");
        }
    }

    private void proc_rm(CCField fld) {
        int ps = pCmd.lastIndexOf(",");
        if (ps > 0) {
            pCmd.setLength(ps - 1);
        }
    }

    public void reset() {
        reset(proc.params());
    }

    public void reset(ICCMap p) {
        pFields.forEach(fld -> {
            fld.getFieldValue(p);
        });
    }

    public List<CCField> pFields() {
        return pFields;
    }

    public String pCmd() {
        return pCmd.toString();
    }
}
