package org.cc.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> loadList(File f, String enc) throws Exception {
        List<String> ret = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), enc));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                ret.add(line);
            }
        } finally {
            br.close();
        }
        return ret;
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

    public static boolean isBOM(byte[] buf) {
        return (buf != null && buf.length > 3
            && (buf[0] & 0xFF) == 0xEF
            && (buf[1] & 0xFF) == 0xBB
            && (buf[2] & 0xFF) == 0xBF);
    }

    private static void _copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

    public static void copy(String src, String dest) throws IOException {
        copy(new File(src), new File(dest));
    }

    public static void copy(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String files[] = src.list();
            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copy(srcFile, destFile);
            }
        } else {
            if(src.exists()) {
                InputStream in = new FileInputStream(src);
                File pfile = dest.getParentFile();
                if (!pfile.exists()) {
                    pfile.mkdirs();
                }
                OutputStream out = new FileOutputStream(dest);
                try {
                    _copy(in, out);
                } finally {
                    in.close();
                    out.close();
                }
            }
        }
    }

}
