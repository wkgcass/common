package net.cassite.pure.data.util;

import java.util.LinkedHashMap;

/**
 * Created by wkgcass on 15/10/13.
 */
public class ConstantMap extends LinkedHashMap<Integer, Object> {
    private int count = 0;

    public int add(Object o) {
        put(++count, o);
        return count;
    }
}
