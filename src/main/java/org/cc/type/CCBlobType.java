/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.cc.data.CCData;
import org.cc.util.CCLogger;

public class CCBlobType extends CCBaseType<byte[]> {

    @Override
    public String dt() {
        return dt_blob;
    }

    @Override
    public byte[] value(Object o, byte[] dv) {
        if (o != null) {
            try {
                if (o instanceof byte[]) {
                    return (byte[]) o;
                } else if (o instanceof String) {
                    return o.toString().getBytes("UTF-8");
                }
                throw new RuntimeException("Can't support  " + o.getClass() + "cast to byte[]");
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public byte[] getRS(ResultSet rs, String name) throws SQLException {
        try {
            InputStream is = rs.getBinaryStream(name);
            return CCData.loadData(is);
        } catch (Exception ex) {
            CCLogger.debug("Can't using getRS ", ex);
        }
        return null;
    }

    @Override
    public void setPS(PreparedStatement ps, int idx, Object value) throws SQLException {
        if (value instanceof InputStream) {
            // 外部傳入要外部close 
            InputStream is = (InputStream) value;
            ps.setBinaryStream(idx, is);
        } else if (value instanceof String) {
            setPS(ps, idx, (String) value);
        } else if (value instanceof byte[]) {
            setPS(ps, idx, (byte[]) value);
        } else if (value == null) {
            ps.setNull(idx, jdbc());
        }
    }

    private void setPS(PreparedStatement ps, int idx, String value) throws SQLException {
        if (value != null) {
            try {
                setPS(ps, idx, value.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setPS(PreparedStatement ps, int idx, byte[] data) throws SQLException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            ps.setBinaryStream(idx, bis, data.length);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Class<?> nativeClass() {
        try {
            return Class.forName("[B");
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public int jdbc() {
        return Types.BLOB;
    }

}
