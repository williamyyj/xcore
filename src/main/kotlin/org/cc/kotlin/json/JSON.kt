package org.cc.kotlin.json

import java.io.File


class JOMap : HashMap<String?, Any>() {

    fun asInt(id : String) : Int {
        return asInt(get(id),0);
    }

    fun asInt(id:String , dv:Int) : Int {
        return asInt(id,dv);
    }

    fun asLong(id : String) : Long {
        return asLong(get(id),0L);
    }

    fun asLong(id : String, dv: Long) : Long {
        return asLong(get(id),dv);
    }

    fun asDouble(id : String) : Double {
        return asDouble(get(id),0.0);
    }

    fun asDouble(id : String, dv:Double) : Double {
        return asDouble(get(id),dv);
    }

    fun asString(id : String) : String {
        return asString(get(id));
    }

    fun asString(id : String, dv:String) : String {
        return asString(get(id),dv);
    }

}

class JOList : ArrayList<Any>(){

    fun asInt(id:Int):Int{
        return asInt(get(id),0);
    }

    fun asInt(id:Int, dv:Int):Int{
        return asInt(get(id),dv);
    }

    fun asLong(id : Int) : Long {
        return asLong(get(id),0L);
    }

    fun asLong(id : Int, dv: Long) : Long {
        return asLong(get(id),dv);
    }

    fun asDouble(id : Int) : Double {
        return asDouble(get(id),0.0);
    }

    fun asDouble(id : Int, dv:Double) : Double {
        return asDouble(get(id),dv);
    }

    fun asString(id : Int) : String {
        return asString(get(id));
    }

    fun asString(id : Int, dv:String) : String {
        return asString(get(id),dv);
    }

}