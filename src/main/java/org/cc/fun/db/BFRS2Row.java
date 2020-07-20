package org.cc.fun.db;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;
import org.cc.CCMap;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.ICCType;
import org.cc.util.CCLogger;

/**
 *
 * @author william
 */
public class BFRS2Row implements BiFunction<ICCList, ResultSet, ICCMap> {

    @Override
    public ICCMap apply(ICCList metadata, ResultSet rs) {
        ICCMap row = new CCMap();
        for(Object o : metadata){
            try {
                ICCMap meta = (ICCMap) o;
                ICCType type = (ICCType) meta.get("type");
                String name = meta.asString("name");
                row.put(name, type.getRS(rs, name));
            } catch (SQLException ex) {
                CCLogger.error("fail : "+o.toString(), ex);
            }
        }
        return row;
    }

}
