package org.cc.util;

import org.cc.ICCMap;
import org.mvel2.MVEL;

/**
 * @author william
 */
public class CCMvel {

    public static Object eval(ICCMap p, String id) {
        return MVEL.eval(p.asString(id),p);
    }
}
