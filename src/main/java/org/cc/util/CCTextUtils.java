package org.cc.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.cc.json.JSONArray;

/**
 *
 * @author william
 */
public class CCTextUtils {

    public static JSONArray lineToJA(Object o) {
        if (o instanceof JSONArray) {
           return (JSONArray) o;
        } else if (o instanceof String){
            String line = ((String)o).trim();
            line = line.charAt(0)=='[' ? line : "["+line+"]";
            return new JSONArray(line);
        }
        return null;
    }
    
    public static Set<String> lineToSet(Object o){
        JSONArray ja = lineToJA(o);
        if(ja!=null){
            Set<String> ret = new HashSet<String>();
            ja.forEach((it) -> { ret.add((String) it);});
            return ret;
        }
        return null;
    }
    
}
