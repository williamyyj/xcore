package org.cc.util;

import java.io.File;
import java.io.Serializable;
import org.cc.data.CCData;

public class CCBuffer implements Serializable, CharSequence {

    protected char[] data;
    protected String enc = "UTF-8";
    protected int ps = 0;
    protected int line = 1;
    protected int pos = 1;
    protected int tab = 0;
    protected File src;

    public CCBuffer(char[] data) {
        this.data = data;
    }

    public CCBuffer(String text) {
        data = text.toCharArray();
    }

    public CCBuffer(File f, String enc) {
        try {
            data = CCData.loadClob(f, enc);
            src = f;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int length() {
        return data.length;
    }

    @Override
    public char charAt(int index) {
        return data[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int size = end - start;
        char[] buf = new char[size];
        System.arraycopy(data, start, buf, 0, size);
        return new CCBuffer(buf);
    }

    public String subString(int start, int end) {
        return new String(data, start, end - start);
    }

    public String subString(int start) {
        return new String(data, start, data.length - start);
    }

    public boolean m(char c) {
        return (data[ps] == c);
    }
    
    public void tk_init(){
        ps =-1 ;
        pos = 0 ;
        line = 1 ;
        tab = 0;
    }

    public boolean m(int idx, char c) {
        return ((ps + idx) < data.length && c == data[ps + idx]);
    }

    public boolean m(String text) {
        return m(ps, text);
    }

    public boolean mi(String text) {
        return mi(ps, text);
    }

    public boolean m(int idx, String text) {
        char[] buf = text.toCharArray();
        for (char c : buf) {
            if ((idx >= data.length || c != data[idx++])) {
                return false;
            }
        }
        return true;
    }

    public boolean mi(int idx, String text) {
        char[] buf = text.toCharArray();
        for (char c : buf) {
            if (idx >= data.length) {
                return false;
            }
            char a = Character.toLowerCase(c);
            char b = Character.toLowerCase(data[idx++]);
            if (a != b) {
                return false;
            }
        }
        return true;
    }

    public boolean in(int idx, String text) {
        return (text.indexOf(data[ps + idx]) >= 0);
    }

    public boolean has() {
        return (ps + 1 < data.length);
    }

    protected char _next() {
        ps++;
        return (data.length > ps) ? data[ps] : 0;
    }

    public char next() {
        char c = _next();
        pos++;
        if (c == 9) {
            tab++;
        }
        if (c == 10 || c == 13) {
            line++;
            pos = 0;
            tab = 0;
            if (c == 13 && ps + 1 < data.length && data[ps + 1] == 10) {
                ps++;

            }
            c = 10;
        }

        return c;
    }

    protected void move(int offset) {
        ps += offset;
        if (ps < data.length) {
            char ch = data[ps];
            if (ch == 10 || ch == 13) {
                //   linux  , os x 
                line++;
                pos = 1;
                tab = 0;
                ps++;
                if (ch == 13 && ps + 1 < data.length && data[ps + 1] == 10) {
                    //  m$ 
                    ps++;
                }
            }
        }
    }

    public String tk_string(char quote) {
        StringBuilder sb = new StringBuilder();
        char ch = 0;
        while ((ch = next()) != 0 && !m(quote)) {
            if (ch == '\\') {
                ch = next();
                switch (ch) {
                    case 'b':
                        sb.append('\b');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 'u':
                        sb.append((char) Integer.parseInt(subString(ps + 1, ps + 5), 16));
                        ps += 4;
                        pos += 4;
                        break;
                    case '"':
                    case '\'':
                    case '\\':
                        sb.append(ch);
                }
            } else {
                sb.append(ch);
            }
        }
        //    m(quote)  check error 
        next(); // skip quote 
        return sb.toString();
    }

    public String tk_text(String pattern) {
        int start = ps;
        while (!m(pattern)) {
            next();
        }
        String text = subString(start, ps);
        if (m(pattern)) {
            ps += pattern.length();
        }
        return text;
    }

    public void tk_csp() {
        while (isWhiteSpace()) {
            next();
        }
    }

    public boolean isWhiteSpace() {
        char c = 0;
        return (ps < data.length && (c = data[ps]) == 9 || c == 10 || c == 13 || c == 32);
    }

    public boolean tk_m(String str) {
        if (m(str)) {
            move(str.length());
            return true;
        }
        return false;
    }
    
    public char ch(){
        return  (data.length > ps) ? data[ps] : 0;
    }

    @Override
    public String toString() {
        return String.format("[line:%s,pos:%s,ps:%s]:%s", line, pos, ps, ch());
    }

    public static void main(String[] args) {
        String text = "<p>@{}</p>";
        char[] cbuf = text.toCharArray();
        CCBuffer buf = new CCBuffer(cbuf);
        String line = buf.tk_text("@{");

        System.out.println(line);
    }

}
