package org.cc.fun.cg;


import java.util.function.BiFunction;
import org.cc.ICCMap;
import org.cc.db.DBCmdPS;
import org.cc.model.CCProcObject;

/**
 *
 * @author william
 */
public class BiJavaBean implements BiFunction<CCProcObject, DBCmdPS, ICCMap>{

    @Override
    public ICCMap apply(CCProcObject t, DBCmdPS u) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
