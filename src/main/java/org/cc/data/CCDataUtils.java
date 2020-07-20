package org.cc.data;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cc.util.CCFunc;

/**
 *
 * @author William
 */
public class CCDataUtils {

    public static byte[] loadData(InputStream is) throws IOException {
        BufferedInputStream bis = null;
        byte[] data = null;
        byte[] tmp = new byte[1024];
        int num = 0;
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
        return data;
    }

    public static char[] loadClob(File f, String enc) throws Exception {
        char[] data = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), enc));
        char[] tmp = new char[4096];
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

    private static boolean isBOM(byte[] buf) {
        return (buf != null && buf.length > 3
          && (buf[0] & 0xFF) == 0xEF
          && (buf[1] & 0xFF) == 0xBB
          && (buf[2] & 0xFF) == 0xBF);
    }

    public static String loadString(File f, String enc) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        try {
            byte[] data = loadData(fis);
            if (isBOM(data)) {
                return new String(data, 3, data.length - 3, enc);
            } else {
                return new String(data, enc);
            }
        } finally {
            fis.close();
        }
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

    public static void saveString(File f, String enc, String text) throws IOException {
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

    public static void save(InputStream in, File f) throws IOException, Exception {
        FileOutputStream out = new FileOutputStream(f);
        try {
            byte[] buffer = new byte[4 * 1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
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
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            try {
                copy(in, out);
            } finally {
                in.close();
                out.close();
            }
        }
    }

    public static void fgroup(String path) throws Exception {
        File f = new File(path);
        if (f.isDirectory()) {
            File[] list = f.listFiles();
            for (File item : list) {
                System.out.println("===== proc " + item);
                file_group(f, item);
            }
        }
    }

    private static void file_group(File p, File item) throws Exception {
        byte[] buf = loadData(item);
        String hash = (String) CCFunc.apply("ende.md5", buf);
        File tp = new File(p, hash);
        if (!tp.exists()) {
            tp.mkdirs();
        }
        File target = new File(tp, item.getName());
        copy(item, target);
    }

    public static byte[] loadData(File f) throws Exception {
        byte[] data = null;
        InputStream br = new FileInputStream(f);
        byte[] tmp = new byte[8192];
        int num = 0;
        int len = 0;
        try {
            while ((num = br.read(tmp)) > 0) {
                if (data == null) {
                    data = new byte[num];
                    System.arraycopy(tmp, 0, data, 0, num);
                } else {
                    byte[] old = data;
                    data = new byte[old.length + num];
                    System.arraycopy(old, 0, data, 0, old.length);
                    System.arraycopy(tmp, 0, data, old.length, num);
                }
                len += num;
            }
        } finally {
            br.close();
        }
        return data;
    }

    public static void safe_dir(String path) {
        safe_dir(new File(path));
    }

    public static void safe_dir(File f) {
        
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static void copy(File fsrc, File fdest, Date toDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

}
