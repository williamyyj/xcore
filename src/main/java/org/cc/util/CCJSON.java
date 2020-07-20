package org.cc.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.cc.CCMap;
import org.cc.ICCList;
import org.cc.ICCMap;

/**
 * @author william
 */
public class CCJSON {

    public static String toString(Map m) {
        return toString(m, 0);
    }

    public static String toString(Map m, int indentFactor) {
        StringWriter w = new StringWriter();
        synchronized (w.getBuffer()) {
            try {
                toString(m, w, indentFactor, 0);
                return w.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    public static String toString(List data) {
        return toString(data, 0);
    }

    public static String toString(List data, int indentFactor) {
        StringWriter w = new StringWriter();
        synchronized (w.getBuffer()) {
            try {
                toString(data, w, indentFactor, 0);
                return w.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    private static void toString(Map m, Writer writer, int indentFactor, int indent) throws IOException {
        if (m == null) {
            return;
        }
        boolean isIndent = !CCCast._bool(m.get("__indent__"), true);

        if (isIndent) {
            m.remove("__indent__");
            indentFactor = 0;
            indent = 0;
        }

        boolean commanate = false;
        final int length = m.size();
        Iterator<String> keys = m.keySet().iterator();
        
          ArrayList<String> list = new ArrayList<String>(m.keySet());
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return "id".equals(o1)  || "id".equals(o2) ? -1 : o1.compareTo(o2);
                }
            });
        
        writer.write('{');

        if (length == 1) {
            Object key = keys.next();
            writer.write(quote(key.toString()));
            writer.write(':');
            if (indentFactor > 0) {
                writer.write(' ');
            }
            print(writer, m.get(key), indentFactor, indent);
        } else if (length > 1) {

            final int newindent = indent + indentFactor;
            //while (keys.hasNext()) {
              //  Object key = keys.next();
              for(String key : list){
                if (commanate) {
                    writer.write(',');
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, newindent);
                writer.write(quote(key.toString()));
                writer.write(':');
                if (indentFactor > 0) {
                    writer.write(' ');
                }
                print(writer, m.get(key), indentFactor, newindent);
                commanate = true;
            }
            if (indentFactor > 0) {
                writer.write('\n');
            }
            indent(writer, indent);
        }
        writer.write('}');
        if (isIndent) {
            m.put("__indent__",false);
        }

    }
    
    


    private static void toString(List list, Writer writer, int indentFactor, int indent) throws IOException {
        boolean commanate = false;
        int length = list.size();
        writer.write('[');

        if (length == 1) {
            print(writer, list.get(0), indentFactor, indent);
        } else if (length != 0) {
            final int newindent = indent + indentFactor;

            for (int i = 0; i < length; i += 1) {
                if (commanate) {
                    writer.write(',');
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, newindent);
                print(writer, list.get(i), indentFactor, newindent);
                commanate = true;
            }
            if (indentFactor > 0) {
                writer.write('\n');
            }
            indent(writer, indent);
        }
        writer.write(']');
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // will never happen - we are writing to a string writer
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.length() == 0) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    w.write('\\');
                    w.write(c);
                    break;
                case '/':
                    if (b == '<') {
                        w.write('\\');
                    }
                    w.write(c);
                    break;
                case '\b':
                    w.write("\\b");
                    break;
                case '\t':
                    w.write("\\t");
                    break;
                case '\n':
                    w.write("\\n");
                    break;
                case '\f':
                    w.write("\\f");
                    break;
                case '\r':
                    w.write("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                      || (c >= '\u2000' && c < '\u2100')) {
                        w.write("\\u");
                        hhhh = Integer.toHexString(c);
                        w.write("0000", 0, 4 - hhhh.length());
                        w.write(hhhh);
                    } else {
                        w.write(c);
                    }
            }
        }
        w.write('"');
        return w;
    }

    private static void print(Writer writer, Object value, int indentFactor, int indent) throws IOException {
        if (value == null) {
            writer.write("null");
        } else if (value instanceof Map) {
            toString((Map) value, writer, indentFactor, indent);
        } else if (value instanceof List) {
            toString((List) value, writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            toString(new ArrayList((Collection<Object>) value), writer, indentFactor, indent);
        } else if (value.getClass().isArray()) {
            toString(Arrays.asList(value), writer, indentFactor, indent);
        } else if (value instanceof Number) {
            writer.write(numberToString((Number) value));
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            quote(sdf.format((Date) value), writer);
        } else {
            quote(value.toString(), writer);
        }
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i += 1) {
            writer.write(' ');
        }
    }

    private static String numberToString(Number number) {
        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
          && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    private static ConcurrentHashMap<String, CCCacheItem> cache;

    public static ConcurrentHashMap cache() {
        if (cache == null) {
            cache = new ConcurrentHashMap<String, CCCacheItem>();
        }
        return cache;
    }

    public static ICCMap load(String base, String id, String suffix) {
        id = id.replace(".", "/");
        return load(new File(base, id + "." + suffix), "UTF-8");
    }

    public static ICCMap load(String base, String fid) {
        return load(base, fid, "json");
    }

    @SuppressWarnings("unchecked")
    public static ICCMap load(File f, String enc) {
        System.out.println(f);
        if (f==null || !f.exists()) {
            return null;
        }
        try {
            String id = f.getCanonicalPath();
            CCCacheItem<ICCMap> item = (CCCacheItem<ICCMap>) cache().get(id);
            if (item != null && item.lastModified >= f.lastModified()) {
                CCLogger.debug("using cache " + item.id + " lastload : " + item.lastModified);
                return item.value;
            } else if (item != null) {
                return reload(f, enc);
            } else if (item == null && f.exists()) {
                return reload(f, enc);
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static synchronized ICCMap reload(File f, String enc) {
        CCCacheItem<ICCMap> item = new CCCacheItem<ICCMap>();
        try {
            item.id = f.getCanonicalPath();
            item.value = new CCJsonParser(f, enc).parser_obj();
            item.lastModified = System.currentTimeMillis();
            cache().put(item.id, item);
            return item.value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ICCMap loadString(String text) {
        return new CCJsonParser(text).parser_obj();
    }

    public static ICCList loadJA(String text) {
        return new CCJsonParser(text).parser_list();
    }

    public static ICCMap line(Object line) {
        if (line instanceof ICCMap) {
            return (ICCMap) line;
        } else if (line instanceof String) {
            String text = ((String) line).trim();
            text = (text.charAt(0) == '{' ? text : "{" + text + "}");
            return loadString(text);
        }
        return null;
    }

    public static ICCMap mix(ICCMap p, ICCMap c) {
        ICCMap ret = new CCMap(p);
        if (c != null) {
            c.forEach((k, v) -> ret.put(k, v));
        }
        return ret;
    }

    public static ICCMap data(ICCMap p, ICCMap c) {
        ICCMap ret = new CCMap();
        p.forEach((k, v) -> {
            if (k.charAt(0) != '$') {
                ret.put(k, v);
            }
        });
        c.forEach((k, v) -> ret.put(k, v));
        return ret;
    }
    
    
    public static ICCMap data(ICCMap p, String cid) {
        
        if(p.containsKey(cid)){
            return data(p,p.map(cid));
        }
        return p;
    }

    public static ICCMap get(ICCList ja, String name, String value) {
        if (ja != null) {
            for (int i = 0; i < ja.size(); i++) {
                ICCMap row = ja.map(i);
                if (row != null) {
                    if (value.equalsIgnoreCase(row.asString("name"))) {
                        return row;
                    }
                }
            }
        }
        return null;
    }

    public static void mix(ICCMap p, ICCMap m, String[] items) {
        for(String item : items){
            if(m.containsKey(item)){
                p.put(item, m.get(item));
            }
        }
    }

}
