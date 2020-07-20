package org.cc.fun.db;

import java.sql.ResultSet;
import java.util.function.BiFunction;
import org.cc.CCList;
import org.cc.ICCList;
import org.cc.ICCMap;

/**
 *
 * @author william
 */
public class BFRS2Rows implements BiFunction<ICCList, ResultSet, ICCList> {

    private BiFunction<ICCList, ResultSet, ICCMap> rs2row = new BFRS2Row();

    @Override
    public ICCList apply(ICCList metadata, ResultSet rs) {
        ICCList data = new CCList();
        try {
            while (rs.next()) {
                data.add(rs2row.apply(metadata, rs));
            }
        } catch (Exception e) {
        }
        return data;
    }

}
