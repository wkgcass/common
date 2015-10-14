package net.cassite.pure.data.util;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by wkgcass on 15/10/13.
 */
public class AliasMap extends HashMap<Field, String> {
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
    public String get(Object field) {
        if (field instanceof Field) {
            if (containsKey(field)) return super.get(field);
            put((Field) field, prefix + (++aliasCount));
            return super.get(field);
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
