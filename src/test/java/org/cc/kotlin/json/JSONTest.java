package org.cc.kotlin.json;

import org.junit.Test;

import java.util.HashMap;

public class JSONTest {
    @Test
    public void test_toString(){
        HashMap<String,Object> jo = new HashMap<String,Object>();

        String ret = JSONOutputKt.toString(jo,1);
        System.out.println(ret);
    }

}
