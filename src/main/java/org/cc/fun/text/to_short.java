package org.cc.fun.text;

import java.util.function.Function;

/**
 *
 * @author william
 */
public class to_short implements Function<String, String> {

    @Override
    public String apply(String text) {
        if (text == null) {
            return null;
        }
        String text1 = text.toLowerCase();
        String[] buf = text1.split("[\\.\\-\\_]");
        StringBuilder sb = new StringBuilder();
        if (buf.length == 1) {
            sb.append(buf[0]);
        } else {
            for (int i = 1; i < buf.length; i++) {
                sb.append(buf[i]);
            }
        }
        return sb.toString();
    }

}
