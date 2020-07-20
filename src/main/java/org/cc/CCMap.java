package org.cc;


import java.util.HashMap;
import java.util.Map;
import org.cc.util.CCJSON;

/**
 *
 * @author william
 */
public class CCMap extends HashMap<String, Object> implements ICCMap {
    
    public CCMap(){
        super();
    }
    
    public CCMap(Map m){
        super(m);
    }
    
    @Override
    public Object put(String k, Object v){
        if(v!=null){
            super.put(k, v);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return CCJSON.toString(this);
    }

}
