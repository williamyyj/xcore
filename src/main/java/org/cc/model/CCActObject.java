package org.cc.model;

import org.cc.CCMap;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.util.CCJSON;

/**
 *   [metaId, actId] 
 * @author william
 */
public class CCActObject extends CCMap {

    private CCProcObject proc;
    private ICCMap mcfg ; 

    public CCActObject(CCProcObject proc, String line) {
        if (line != null && line.charAt(0) != '[') {
            line = "[" + line + "]";
        }
        __init__(proc, CCJSON.loadJA(line));
    }

    public CCActObject(CCProcObject proc, ICCList ja) {
        __init__(proc, ja);
    }

    private void __init__(CCProcObject proc, ICCList ja) {
        this.proc = proc;
        mcfg = CCJSON.load(proc.base()+"/module", ja.asString(0));
        if(ja.size()>1){
            putAll(mcfg.map(ja.asString(1)));
        }
        __init_metadata();  // 處理定義欄立
        __init_model();
    }

    public CCProcObject proc(){
        return proc;
    }

    private void __init_model() {
        put("$proc",proc);
        put("$mcfg",mcfg);
    }

    private void __init_metadata() {//相容
      ICCList list = mcfg.list("$metadata");
      list.forEach(o->{
          System.out.println("===== metaId :"+o);
          proc.metadata(o.toString()); // load meta
      });
    }

}
