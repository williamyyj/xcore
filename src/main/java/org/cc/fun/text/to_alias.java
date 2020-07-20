package org.cc.fun.text;

import java.util.function.Function;

/**
 *
 * @author william
 */
public class to_alias implements Function<String, String> {

    @Override
    public String apply(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        char[] buf = text.toLowerCase().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            char c = buf[i];
            if (c == '.' || c == '-' || c == '_') {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
