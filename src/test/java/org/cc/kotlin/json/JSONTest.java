package org.cc.kotlin.json;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONTest {

    public void test_toString(){

    }


    public void test_moshi() throws IOException {
        String json="{\"age\":\"20\"}";
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Map> jsonAdapter = moshi.adapter(Map.class);
        Map m = jsonAdapter.fromJson(json);
        System.out.println(m);
    }

    @Test
    public void test_jo() {
        JOMap jo = JSONKt.loadJO("abc:5,x:'6',y:zzz");
        System.out.println(jo);
        JOArray ja = JSONKt.loadJA("123,456,'xxxx'");
        System.out.println(ja);
    }

}
