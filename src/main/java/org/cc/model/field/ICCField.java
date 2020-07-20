package org.cc.model.field;

import org.cc.ICCInit;
import org.cc.ICCMap;
import org.cc.ICCType;

/**
 * @author william
 */
public interface ICCField extends ICCInit<ICCMap> {

    public String ct(); // database  P , M   

    public String dt(); //  主型別

    public String ft();

    public String name();

    public String label();

    public String alias();

    public String id();

    public String fmt(); // 格式化使用

    public String notes();

    public int dtSize();

    public ICCType<?> type();

    public ICCMap cfg();

    public void setFieldValue(ICCMap row, Object value);

    public Object getFieldValue(ICCMap row);

}
