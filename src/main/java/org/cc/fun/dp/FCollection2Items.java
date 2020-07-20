package org.cc.fun.dp;

import java.util.Collection;
import java.util.function.Function;

public class FCollection2Items implements Function<Collection, String> {

    @Override
    public String apply(Collection data) {
        StringBuilder sb = new StringBuilder();
        for (Object o : data) {
            if (o instanceof Number) {
                sb.append(o).append(',');
            } else {
                sb.append('\'').append(o).append('\'').append(',');
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

}
