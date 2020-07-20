package org.cc.data;

import java.io.*;

public class CCData {

    public final static int buf_size = 8192;

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

    public static char[] loadClob(File f, String enc) throws Exception {
        char[] data = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), enc));
        char[] tmp = new char[buf_size];
        int num = 0;
        try {
            while ((num = br.read(tmp)) > 0) {
                if (data == null) {
                    data = new char[num];
                    System.arraycopy(tmp, 0, data, 0, num);
                } else {
                    char[] old = data;
                    data = new char[old.length + num];
                    System.arraycopy(old, 0, data, 0, old.length);
                    System.arraycopy(tmp, 0, data, old.length, num);
                }
            }
        } finally {
            br.close();
        }
        return data;
    }

    public static String loadString(File f, String enc) throws Exception {
        byte[] buf = loadData(new FileInputStream(f));
        return (buf != null) ? new String(buf, enc) : null;
    }

    public static void saveText(String text, File f, String enc) throws Exception {
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(f), enc);
            osw.write(text);
            osw.flush();
        } finally {
            if (osw != null) {
                osw.close();
            }
        }
    }

    public static void saveData(byte[] data, OutputStream os) throws Exception {
        try {
            if (data != null && data.length > 0) {
                os.write(data);
                os.flush();
            }
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static String toString(byte[] data, String enc) throws Exception {
        // FIX UTF-8 BOM 
        //System.out.println( data[0]+":" +data[1] + ":" +data[2]);
        if (data != null && data.length > 3 && data[0] == -17 && data[1] == -69 && data[2] == -65) {
            //if(data[0]==0xEF && data[1]==0xBB && data[2]==0xBF){
            byte[] old = data;
            data = new byte[data.length - 3];
            System.arraycopy(old, 3, data, 0, data.length);
            old = null; // gc
        }
        return new String(data, enc);
    }

}
