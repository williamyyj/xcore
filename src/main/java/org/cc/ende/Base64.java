package org.cc.ende;

public class Base64 {

    // std base64

    private static final char[] b64_ens = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    // change url safe 
    private static final char[] u64_ens = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();

    private static final byte[] b64_des = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 64, -1, -1,
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    private static final byte[] u64_des = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 64, -1, -1,
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    private static final char xEnd = '=';

    public static String encode(byte[] raw, char[] en) {
        StringBuilder sb = new StringBuilder();
        int len = raw.length;
        int i = 0;
        int c1, c2, c3;
        while (i < len) {
            c1 = raw[i++] & 0xff;
            sb.append(en[c1 >> 2]);
            if (i == len) {
                sb.append(en[(c1 & 0x3) << 4]);
                sb.append(xEnd);
                sb.append(xEnd);
                break;
            }
            c2 = raw[i++] & 0xff;
            if (i == len) {
                sb.append(en[(c1 & 0x3) << 4 | (c2 & 0xf0) >> 4]);
                sb.append(en[(c2 & 0xf) << 2]);
                sb.append(xEnd);
                break;
            }
            c3 = raw[i++] & 0xff;
            sb.append(en[(c1 & 0x3) << 4 | (c2 & 0xf0) >> 4]);
            sb.append(en[(c2 & 0xf) << 2 | (c3 & 0xc0) >> 6]);
            sb.append(en[c3 & 0x3f]);
        }
        return sb.toString();
    }

    public static byte[] decode(String text, byte[] de) {
        int pad = 0;
        String base64 = text.trim();
        char[] buf = base64.toCharArray();
        for (int i = buf.length - 1; buf[i] == '='; i--) {
            pad++;
        }
        int len = base64.length() * 6 / 8 - pad;
        byte[] raw = new byte[len];
        int id = 0;
        int c1, c2, c3, c4;
        int i = 0;
        while (i < buf.length) {
            while ((c1 = de[buf[i++]]) == -1);
            while ((c2 = de[buf[i++]]) == -1);
            while ((c3 = de[buf[i++]]) == -1);
            while ((c4 = de[buf[i++]]) == -1);
            raw[id++] = (byte) ((c1 << 2) | (c2 & 0x30) >> 4);
            if (c3 == 64) {
                break;
            }
            raw[id++] = (byte) (((c2 & 0xf) << 4) | ((c3 & 0x3c) >> 2));
            if (c4 == 64) {
                break;
            }
            raw[id++] = (byte) (((c3 & 3) << 6) | c4);
        }
        if (id == raw.length) {
            return raw;
        }
        byte[] data = new byte[id];
        System.arraycopy(raw, 0, data, 0, id);
        return data;
    }

    public static String b64_encode(byte[] raw) {
        return encode(raw, b64_ens);
    }

    public static String u64_encode(byte[] raw) {
        return encode(raw, u64_ens);
    }

    public static byte[] b64_decode(String base64) {
        return decode(base64, b64_des);
    }

    public static byte[] u64_decode(String base64) {
        return decode(base64, u64_des);
    }

}
