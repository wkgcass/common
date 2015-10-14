package net.cassite.pure.data.util;

import java.util.HashMap;

/**
 * Created by wkgcass on 15/10/13.
 */
public class AliasMap extends HashMap<Class<?>, String> {
    private final String prefix;
    private int aliasCount = 0;

    public AliasMap(String prefix) {
        this.prefix = prefix;
    }

    public AliasMap(String prefix, int count) {
        this.prefix = prefix;
        this.aliasCount = count;
    }

    @Override
    public String get(Object cls) {
        if (cls instanceof Class) {
            if (containsKey(cls)) return super.get(cls);
            put((Class<?>) cls, prefix + (++aliasCount));
            return super.get(cls);
        } else {
            return null;
        }
    }

    public int getAliasCount() {
        return aliasCount;
    }

    public void setAliasCount(int count) {
        this.aliasCount = count;
    }
}
