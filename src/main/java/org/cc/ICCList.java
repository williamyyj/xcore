/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc;

import java.util.Date;
import java.util.List;

/**
 *
 * @author william
 */
public interface ICCList extends List<Object> {

    public int asInt(int idx, int dv);

    public int asInt(int idx);

    public long asLong(int idx, Long dv);

    public long asLong(int idx);

    public double asDouble(int idx, double dv);
    
    public double asDouble(int idx);

    public Date asDate(int idx);

    public ICCMap map(int idx);

    public ICCList list(int idx);

    public String asString(int idx, String dv);
    
    public String asString(int idx);

    public String toString(int indent);
    
    default <T> T as(Class<T> cls, int idx){
        return (T) get(idx);
    }

}
