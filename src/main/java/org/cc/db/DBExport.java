package org.cc.db;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.cc.CCMap;
import org.cc.model.CCProcObject;
import org.cc.ICCBatch;
import org.cc.ICCMap;
import org.cc.data.CCDataUtils;
import org.cc.model.CCWorkObject;
import org.cc.util.CCFunc;

/**
 * @author william
 */
public class DBExport implements ICCBatch {

    private String base;
    protected CCProcObject proc;
    protected NumberFormat nf = new DecimalFormat("000000");

    public DBExport(String base) {
        this.base = base;
        proc = new CCProcObject(base);
    }

    @Override
    public void execute(ICCMap p) throws Exception {

        String table = p.asString("$table");
        int numPage = 1;
        CCWorkObject wo = new CCWorkObject(proc, null);
        ICCMap evn = new CCMap();
        evn.put("$cmd", "select * from " + table);
        evn.put("$orderby", "getdate()");
        wo.reset(p);
        wo.setEvent(evn);
        CCFunc.apply("wo_page", wo);
        ICCMap firstRet = proc.map("$ret");
        int total = firstRet.asInt("total");
        numPage = p.asInt("numPage", 1000);
        int last = (total / numPage) + ((total % numPage > 0) ? 1 : 0);
        File root = new File(base, "data/export");
        File first = new File(root, table.toLowerCase() + ".json");
        CCDataUtils.safe_dir(root);
        CCDataUtils.saveString(first, "UTF-8", firstRet.toString(4));

        for (int i = 0; i < last; i++) {
            p.put("page", i + 1);
            p.put("numPage", numPage);
            wo.reset(p);
            CCFunc.apply("wo_page", wo);
            ICCMap ret = proc.map("$ret");
            String ejo = (String) CCFunc.apply("ende.zip_encode", ret.toString());
            File dest = new File(root, table.toLowerCase() + nf.format(i + 1) + ".ejo");
            System.out.println(dest);
            CCDataUtils.saveString(dest, "UTF-8", ejo);
        }
    }

    @Override
    public void release() throws Exception {
        if (proc != null) {
            proc.release();
        }
    }

    public CCProcObject proc() {
        return this.proc;
    }

}
