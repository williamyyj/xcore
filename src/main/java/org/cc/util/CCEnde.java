package org.cc.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 * @author william
 */
public class CCEnde {

    private static int buf_size = 4096;

    public static String toHexString(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int b = 0xff & data[i];
            hexString.append((b > 15) ? Integer.toHexString(0xFF & data[i]) : '0' + Integer.toHexString(0xFF & data[i]));
        }
        return hexString.toString();
    }

    public static String digest(MessageDigest algorithm, InputStream is) throws Exception {
        BufferedInputStream bis = null;
        byte[] buf = new byte[buf_size];
        int num = 0;
        try {
            bis = new BufferedInputStream(is);
            algorithm.reset();
            algorithm.update(buf, num, num);
            while ((num = bis.read(buf)) > 0) {
                algorithm.update(buf, 0, num);
            }
            return toHexString(algorithm.digest());
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    public static byte[] loadData(InputStream is) throws Exception {
        BufferedInputStream bis = null;
        byte[] data = null;
        byte[] tmp = new byte[buf_size];
        int num = 0;
        try {
            bis = new BufferedInputStream(is);
            while ((num = bis.read(tmp)) > 0) {
                if (data == null) {
                    data = new byte[num];
                    System.arraycopy(tmp, 0, data, 0, num);
                } else {
                    byte[] old = data;
                    data = new byte[old.length + num];
                    System.arraycopy(old, 0, data, 0, old.length);
                    System.arraycopy(tmp, 0, data, old.length, num);
                }
            }
        } finally {
            bis.close();
        }
        return data;
    }

}
