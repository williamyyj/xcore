package org.cc;

import java.util.ArrayList;
import java.util.Date;
import org.cc.util.CCCast;
import org.cc.util.CCJSON;

/**
 *
 * @author william
 */
public class CCList extends ArrayList<Object> implements ICCList{

    @Override
    public int asInt(int idx, int dv) {
        return CCCast._int(get(idx), dv);
    }

    @Override
    public long asLong(int idx, Long dv) {
        return CCCast._long(get(idx), dv);
    }

    @Override
    public double asDouble(int idx, double dv) {
        return CCCast._double(get(idx), dv);
    }

    @Override
    public Date asDate(int idx) {
        return CCCast._date(get(idx));
    }

    @Override
    public String asString(int idx, String dv) {
        return CCCast._string(get(idx));
    }

    @Override
    public  String toString(){
        return CCJSON.toString(this);
    }

    @Override
    public int asInt(int idx) {
        return asInt(idx,0);
    }

    @Override
    public long asLong(int idx) {
        return asLong(idx,0L);
    }

    @Override
    public double asDouble(int idx) {
        return asDouble(idx,0.0);
    }

    @Override
    public String asString(int idx) {
        return asString(idx,"");
    }

    @Override
    public String toString(int indent) {
        return CCJSON.toString(this, indent);
    }

    @Override
    public ICCMap map(int idx) {
        Object o = get(idx);
        return (o instanceof ICCMap) ? (ICCMap) o : null;
    }

    @Override
    public ICCList list(int idx) {
        Object o = get(idx);
        return (o instanceof ICCList) ? (ICCList) o : null;
    }
    
}
