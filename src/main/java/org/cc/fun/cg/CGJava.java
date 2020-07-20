package org.cc.fun.cg;


import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author william
 */
public class CGJava {

    public static Map<String, String> dt2NativeMap;

    static {
        dt2NativeMap = new HashMap<>();
        dt2NativeMap.put("string", "String");
        dt2NativeMap.put("date", "java.util.Date");
        dt2NativeMap.put("int", "Integer");
        dt2NativeMap.put("long", "Long");
        dt2NativeMap.put("double", "Double");
    }


}
